<template>
  <v-dialog v-model="isDialog" width="600" persistent>
    <v-card>
      <v-card-title>[{{ deviceName }} の最新ログ]</v-card-title>
      <v-container>
        <div class="text-container">
          <div v-for="(line, index) in textLines" :key="index">
            <div v-if="line.status">
              <I
                ><small>{{ line.date }}</small></I
              ><br />&nbsp;&nbsp;{{ line.message }}
            </div>
            <div v-else style="color: red">
              <I
                ><small>{{ line.date }}</small></I
              ><br />&nbsp;&nbsp;{{ line.message }}
            </div>
          </div>
        </div>
      </v-container>
      <v-card-actions class="justify-center">
        <v-btn
          color="primary"
          class="ma-2 white--text"
          elevation="2"
          @click="close()"
          >閉じる</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
    
  <script>
import { useGetLog } from "@/api/ManagementScreenTop/MSDevice";

export default {
  data() {
    return {
      isDialog: false,
      deviceName: null,
      textLines: [],
    };
  },

  methods: {
    // ======================================================
    // 表示設定
    // ======================================================
    initialize: function (deviceName, id, type) {
      this.deviceName = deviceName;
      useGetLog(id, type)
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status != 0) {
            throw new Error(message);
          }
          console.log(data);
          this.textLines = data;
        })
        .catch((error) => {
          //失敗時
          alert("ログ情報の取得に失敗しました。");
          console.log(error);
        });
    },
    // ======================================================
    // ダイアログを閉じる
    // ======================================================

    // ======================================================
    // ダイアログを閉じる
    // ======================================================
    close: function () {
      this.isDialog = false;
    },
  },
};
</script>
  <style lang="scss">
.text-container {
  height: 400px;
  overflow-y: auto;
  border: 1px solid #ccc; /* Optional: Add a border for better visibility */
}
</style>
    