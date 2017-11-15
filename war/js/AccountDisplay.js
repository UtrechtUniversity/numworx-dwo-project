function AccountDisplay() {
                var username = "";
                var firstName = "";
                var insertion = "";
                var familyName = "";
                var email = "";
                var password = "";
                var newPassword = "";
                var newPasswordAgain = "";
}

                AccountDisplay.prototype.clear = function () {
                    username = "";
                    firstName = "";
                    insertion = "";
                    familyName = "";
                    email = "";
                }

                AccountDisplay.prototype.init = function () {
                    password = "";
                }
                AccountDisplay.prototype.updateView = function (u, f, i, n, e) {
                    username = u;
                    firstName = f;
                    insertion = i;
                    familyName = n;
                    email = e;
                }
            
            window.jsAccountDisplay = new AccountDisplay();
