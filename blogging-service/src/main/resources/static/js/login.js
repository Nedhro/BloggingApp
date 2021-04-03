let baseUrl = "/api/v1";

function loginUser() {
    let username = $("#username").val();
    let password = $("#password").val();

    let userData = {
        "username": username,
        "password": password
    };
    console.log(userData);
    $.ajax({
        type: 'POST',
        url: baseUrl + "/auth/login",
        data: JSON.stringify(userData),
        dataType: 'json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        },
        success: function (result) {
            console.log(result);
        },
        error: function (jqXHR) {
            console.log(jqXHR);
        }
    });
}
