const app = new Vue(
    {
        el: "#vue_div",
        data: {
                title: "",
                caption: "",
                error_message: "",
                uploaded_image_base64: ""
        },
        methods: {
                previewMediaFile: function () {
                        let files = this.$refs.file_upload.files;
                        let reader = new FileReader();
                        if (files && files[0]) {
                            reader.onload = (e) => {
                                this.uploaded_image_base64 = e.target.result;
                            }
                            let upload = reader.readAsDataURL(files[0]);
                        }
                },

                uploadMediaFile: function() {
                        var self = this;
                        
                        apiUploadMedia(this.$refs.file_upload)
                            .then(function(media_id) {
                                console.log(`Media upload successful, returned media_id ${media_id}.`);
                                self.uploaded_image_url = `/get_media?media_id=${media_id}`;
                            })
                            .catch(function(error) {
                                self.error_message = error;
                            });
                }
        }
    }
);
