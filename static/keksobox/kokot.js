$(() => {
    /* boa pixi fess nehekujce mi toten javaskript bo fess malo času na to ;))) */
    console.log("loeaded")
    $("#sendMessageButtonBlyat").click(() => {
        console.log("XD")
        let name = $("#name").val()
        let email = $("#email").val()
        let phone = $("#phone").val()
        let message = $("#message").val()

        let outmsg = ("=== Keksobox ===<br>" +
            name + "<br>" +
            email + "<br>" +
            phone + "<br>" + "======="  + "<br>" +
            message + "<br>"
        )

        
        if (name != "" && email != "" && phone != "" && message != "") {
            alert(name)
            Email.send({
                Host : "smtp.elasticemail.com",
                Username : "firmagymind@gmail.com",
                Password : "97cc8eb5-c10d-4e7d-b699-58c3b72014dd",
                To : 'firmagymind@gmail.com',
                From : "firmagymind@gmail.com",
                Subject : "Keksobox Mail - " + email,
                Body : outmsg
            }).then(
                message => alert(message)
            )
        } else {
            alert("Zadajte prosím všetky polia!")
        }

    })

    
})