// Put functions here that access API functionality from the backend.

function apiDecodeJsonResponse(raw_json) {
    // Strip the "for(;;);" from the response (used to block some cross-site hacks) and decode the JSON.
    return JSON.parse(raw_json.substring("for(;;);".length));
}

function apiUserExists(username_or_email) {
    return new Promise((resolve, reject) => {
        axios.get("/user_exists", {
            params: {
                username_or_email: username_or_email
            }
        })
            .then(function(response) {
                var decoded = apiDecodeJsonResponse(response.data);
                resolve(decoded["result"]);
            })
            .catch(function(error) {
                console.log("User exists endpoint error.");
                reject(false);
            });
    });
}

function apiLoginUser(username_or_email, password) {
    return new Promise((resolve, reject) => {
            const params = new URLSearchParams();
            params.append("username_or_email", username_or_email);
            params.append("password", password);

            axios.post("/login_user", params)
                .then(function (response) {
                    document.cookie = "logged_in=true;"
                    resolve("Login successful.");
                })
                .catch(function (error) {
                    reject("Login failed. Please try again.");
                });
        }
    );
}

function apiCreateUser(username, display_name, password, email) {
    return new Promise((resolve, reject) => {
            const params = new URLSearchParams();
            params.append("username", username);
            params.append("display_name", display_name);
            params.append("password", password);
            params.append("email", email);

            axios.post("/create_user", params)
                .then(function(response) {
                    resolve("User created successfully.");
                })
                .catch(function(error) {
                    reject(error.response.data);
                });
        }
    );
}

function apiLogoutUser() {
    return new Promise((resolve, reject) => {
            axios.post("/logout_user", new URLSearchParams())
                .then(function(response) {
                    document.cookie = "logged_in=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                    resolve("User logged out successfully.");
                })
                .catch(function(error) {
                    reject(error.response.data);
                });
        }
    );
}

function apiCreateCard(title, caption, tags, file_upload_input) {
    return new Promise((resolve, reject) => {
        const params = new FormData();
        params.append("title", title);
        params.append("caption", caption);
        params.append("tags", tags);
        params.append("media_file", file_upload_input.files[0]);

        axios.post("/create_card", params, {headers: {"content-type": "multipart/form-data"}})
            .then(function(response) {
                resolve(parseInt(response.data));
            })
            .catch(function(error) {
                reject(error.response.data);
            });
    });
}

function apiGetCard(card_id) {
    return new Promise((resolve, reject) => {
        axios.get("/get_card", {
            params: {
                card_id: card_id
            }
        })
            .then(function(response) {
                var decoded = apiDecodeJsonResponse(response.data);
                resolve(decoded);
            })
            .catch(function(error) {
                console.log("Get card endpoint error.");
                reject();
            });
    });
}

function apiGetCards(tagged_width, top, title_contains, caption_contains) {
    var get_parameters = {};
    if (tagged_width != null) {
        get_parameters.tagged_with = tagged_width;
    }
    if (top != null) {
        get_parameters.top = top;
    }
    if (title_contains != null) {
        get_parameters.title_contains = title_contains;
    }
    if (caption_contains != null) {
        get_parameters.caption_contains = caption_contains;
    }

    return new Promise((resolve, reject) => {
        axios.get("/get_cards", {
            params: get_parameters
        })
            .then(function(response) {
                var decoded = apiDecodeJsonResponse(response.data);
                resolve(decoded["result"]);
            })
            .catch(function(error) {
                console.log("Get cards endpoint error.");
                reject();
            });
    });
}

function apiCreateOrFindTag(content) {
    return new Promise((resolve, reject) => {
            const params = new URLSearchParams();
            params.append("content", content);

            axios.post("/create_or_find_tag", params)
                .then(function (response) {
                    resolve(parseInt(response.data));
                })
                .catch(function (error) {
                    console.log("Create or find tag endpoint error.");
                    reject();
                });
        }
    );
}

function apiTagCard(card_id, tag_id) {
    return new Promise((resolve, reject) => {
            const params = new URLSearchParams();
            params.append("card_id", card_id);
            params.append("tag_id", tag_id);

            axios.post("/tag_card", params)
                .then(function (response) {
                    resolve();
                })
                .catch(function (error) {
                    console.log("Tag card endpoint error.");
                    reject();
                });
        }
    );
}

function apiGetTags(content_contains, top) {
    var get_parameters = {};
    if (content_contains != null) {
        get_parameters.content_contains = content_contains;
    }
    if (top != null) {
        get_parameters.top = top;
    }

    return new Promise((resolve, reject) => {
        axios.get("/get_tags", {
            params: get_parameters
        })
            .then(function(response) {
                var decoded = apiDecodeJsonResponse(response.data);
                resolve(decoded["result"]);
            })
            .catch(function(error) {
                console.log("Get tags endpoint error.");
                reject();
            });
    });
}

function apiLikeCard(card_id) {
    return new Promise((resolve, reject) => {
            const params = new URLSearchParams();
            params.append("card_id", card_id);

            axios.post("/like_card", params)
                .then(function (response) {
                    resolve();
                })
                .catch(function (error) {
                    console.log("Like card endpoint error.");
                    reject(error.response.data);
                });
        }
    );
}

function apiGetUserInfo() {
    return new Promise((resolve, reject) => {
        axios.get("/get_user_info", {
        })
            .then(function(response) {
                var decoded = apiDecodeJsonResponse(response.data);
                resolve(decoded);
            })
            .catch(function(error) {
                console.log("Get user info endpoint error.");
                reject(error.response.data);
            })
    });
}

function apiGetCardLiked(card_id) {
    var get_parameters = {};
    get_parameters.card_id = card_id;

    return new Promise((resolve, reject) => {
        axios.get("/get_card_liked", {
            params: get_parameters
        })
            .then(function(response) {
                var decoded = apiDecodeJsonResponse(response.data);
                resolve(decoded["result"]);
            })
            .catch(function(error) {
                console.log("Get card liked endpoint error.");
                reject();
            });
    });
}

function apiSignUpEmail(email_address, first_name, last_name) {
    return new Promise((resolve, reject) => {
            const params = new URLSearchParams();
            params.append("email_address", email_address);
            params.append("first_name", first_name);
            params.append("last_name", last_name);

            axios.post("/sign_up_email", params)
                .then(function(response) {
                    resolve();
                })
                .catch(function(error) {
                    console.log("Sign up email endpoint error.");
                    reject(error.response.data);
                })
        }
    );
}