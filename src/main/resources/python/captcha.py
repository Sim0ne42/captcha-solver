import base64
import numpy as np
import psycopg2
import sys
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers

img_width = 200
img_height = 50
max_length = 5
characters = ['2', '3', '4', '5', '6', '7', '8', 'b', 'c', 'd', 'e', 'f', 'g', 'm', 'n', 'p', 'w', 'x', 'y']
char_to_num = layers.StringLookup(vocabulary=list(characters), mask_token=None)
num_to_char = layers.StringLookup(vocabulary=char_to_num.get_vocabulary(), mask_token=None, invert=True)


class CTCLayer(layers.Layer):
    def __init__(self, name=None):
        super().__init__(name=name)
        self.loss_fn = keras.backend.ctc_batch_cost

    def call(self, y_true, y_pred):
        batch_len = tf.cast(tf.shape(y_true)[0], dtype='int64')
        input_length = tf.cast(tf.shape(y_pred)[1], dtype='int64')
        label_length = tf.cast(tf.shape(y_true)[1], dtype='int64')
        input_length = input_length * tf.ones(shape=(batch_len, 1), dtype='int64')
        label_length = label_length * tf.ones(shape=(batch_len, 1), dtype='int64')
        loss = self.loss_fn(y_true, y_pred, input_length, label_length)
        self.add_loss(loss)
        return y_pred


def decode_batch_predictions(pred):
    input_len = np.ones(pred.shape[0]) * pred.shape[1]
    results = keras.backend.ctc_decode(pred, input_length=input_len, greedy=True)[0][0][:, :max_length]
    output_text = []
    for res in results:
        res = tf.strings.reduce_join(num_to_char(res)).numpy().decode('utf-8')
        output_text.append(res)
    return output_text


def encode_img(img_path):
    img = tf.io.read_file(img_path)
    img = tf.io.decode_png(img, channels=1)
    img = tf.image.convert_image_dtype(img, tf.float32)
    img = tf.image.resize(img, [img_height, img_width])
    img = tf.transpose(img, perm=[1, 0, 2])
    return img


def load_model(model_path):
    model_path = model_path + 'model.keras'
    custom_objects = {'CTCLayer': CTCLayer}
    with keras.utils.custom_object_scope(custom_objects):
        loaded_model = tf.keras.models.load_model(model_path)
    prediction_model = keras.models.Model(loaded_model.get_layer(name='image').input,
                                          loaded_model.get_layer(name='dense2').output)
    return prediction_model


def predict_value(model_path, img_id):
    conn = psycopg2.connect(database="postgres", user='postgres', password='docker', host='127.0.0.1', port='5432')
    cursor = conn.cursor()
    cursor.execute("SELECT c.image from captcha c WHERE c.id = {}".format(img_id))
    encoded_data = cursor.fetchone()
    conn.close()

    if encoded_data is None:
        return ''

    decoded_data = base64.b64decode(bytes(encoded_data[0]))
    img_path = 'tmp.png'
    img_file = open(img_path, 'wb')
    img_file.write(decoded_data)
    img_file.close()

    prediction_model = load_model(model_path)
    image = np.expand_dims(encode_img(img_path), axis=0)
    predicted_value = decode_batch_predictions(prediction_model.predict(image))[0]
    return predicted_value


if __name__ == '__main__':
    if len(sys.argv) == 3:
        print(predict_value(sys.argv[1], sys.argv[2]))
    else:
        print('error')
