const captchaImage = document.querySelector(".captcha_image");
const refreshButton = document.querySelector(".refresh_button");
const captchaInputBox = document.querySelector(".captcha_input input");
const message = document.querySelector(".message");
const submitButton = document.querySelector(".button");

let captchaId = null;

const generateCaptcha = async () => {
    captchaImage.src = "images/loading.gif";
    const response = await fetch("http://localhost:8080/captcha/random", {
        method: "GET",
        headers: {
            "Accept": "application/json"
        }
    });
    if (response.status === 200) {
        const json = JSON.parse(await response.text());
        captchaId = json.id;
        captchaImage.src = `data:image/${json.format};base64,${json.base64Image}`;
    }
};

const refreshButtonClick = () => {
    generateCaptcha().then(() => {
        captchaInputBox.value = "";
        captchaKeyUpValidate();
    }).catch(() => handleErrors());
};

const captchaKeyUpValidate = () => {
    submitButton.classList.toggle("disabled", !captchaInputBox.value);
    if (!captchaInputBox.value) {
        message.classList.remove("active");
    }
};

const submitButtonClick = async () => {
    const response = await fetch("http://localhost:8080/captcha/verify", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({"captchaId": captchaId, "text": captchaInputBox.value})
    }).catch(() => handleErrors());
    message.classList.add("active");
    if (response.status === 200) {
        message.innerText = "Entered captcha is correct";
        message.style.color = "#25cd25";
    } else if (response.status === 403) {
        message.innerText = "Entered captcha is not correct";
        message.style.color = "#FF2525";
    } else {
        handleErrors()
    }
};

const handleErrors = () => {
    message.classList.add("active");
    message.innerText = "An error occurred";
    message.style.color = "#FF2525";
}

refreshButton.addEventListener("click", refreshButtonClick);
captchaInputBox.addEventListener("keyup", captchaKeyUpValidate);
submitButton.addEventListener("click", submitButtonClick);

generateCaptcha().catch(() => handleErrors());
