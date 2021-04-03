let baseUrl = "/api/v1";

function saveUser() {
    let fullName = $("#fullName").val();
    let username = $("#username").val();
    let email = $("#email").val();
    let password = $("#password").val();

    let userData = {
        "fullName": fullName,
        "username": username,
        "email": email,
        "password": password
    };
    console.log(userData);
    $.ajax({
        type: 'POST',
        url: baseUrl + "/users",
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
