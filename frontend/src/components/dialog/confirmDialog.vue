<template>

        <v-dialog v-model="isDialog" width="300px">
            <v-card>
                <v-card-title class="dialog-warning-header">
                    <div>確認画面</div>
                </v-card-title>
                <v-card-text class="data-contents">
                    <v-card flat elevation="1">
                        <v-container fluid>
                            <v-row>
                                <v-col col="12">{{ message }}</v-col></v-row>
                        </v-container>
                        <v-card-actions style="text-align:center" width="100%">
                            <div class="text-center">
                                <v-btn class="ma-2" color="primary" @click="doAction(true)">{{ OKAction }}
                                </v-btn>
                                <v-btn class="ma-2" color="primary" @click="doAction(false)">{{
                                    nglabel
                                    }}</v-btn>
                            </div>
                        </v-card-actions>
                    </v-card>
                </v-card-text>
            </v-card>
        </v-dialog>

</template>

<script>
    export default {
        props: { shared /** DialogController */: { required: true } },

        data() {
            return {
                isDialog: false,
                message: "",
                OKAction: "",
                nglabel: ""
            };
        },
        // **********************************************************
        // DOM作成後
        // **********************************************************
        mounted() {
            this.shared.mount(this);
        },
        // **********************************************************
        // 関数
        // **********************************************************
        methods: {
            // ======================================================
            // データの初期化
            // ======================================================
            initialize(message, OKAction, nglabel) {
                this.message = message;
                this.OKAction = OKAction;
                this.nglabel = nglabel;
            },
            // ======================================================
            // ボタン選択時
            // ======================================================
            doAction(mode) {
                this.isDialog = false;
                this.shared.onConclude(mode);
            }
        }
    };
</script>