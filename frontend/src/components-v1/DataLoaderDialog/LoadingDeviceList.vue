<template>
  <v-app>
    <v-dialog v-model="isDialog" width="60%" persistent>
      <v-card>
        <v-card-title>全センサーデータのロード</v-card-title>

        <v-card-text>
          <v-data-table :headers="headers" :items="devices" dense elevation-6>
          </v-data-table>
        </v-card-text>

        <v-card-actions>
          <v-btn
            color="primary"
            class="ma-2 white--text"
            elevation="2"
            @click="callLoaderAPI()"
            :disabled="isRunning == true"
            >全ての再ロードを実行する</v-btn
          >

          <v-btn
            color="primary"
            class="ma-2 white--text"
            elevation="2"
            @click="callLoaderSelected()"
            :disabled="isRunning == true"
            >再ロード(未完了のみ)</v-btn
          >

          <v-btn
            elevation="2"
            @click="isDialog = false"
            :disabled="isRunning == true"
            >閉じる</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-app>
</template>
  
<script>
import {
  useGetSchedule,
  useLoadData,
} from "@/api/ManagementScreenTop/MSDevice";

const HEADERS = [
  { text: "デバイス名", value: "name", sortable: true, width: "40%" },
  { text: "ID", value: "id", sortable: true, width: "10%" },
  { text: "ステータス", value: "status", sortable: true, width: "50%" },
];

export default {
  props: { shared /** DialogController */: { required: true } },

  data() {
    return {
      headers: HEADERS,
      devices: [],
      isDialog: false,
      isRunning: false,
      count: 0,
    };
  },
  mounted() {
    this.shared.mount(this);
  },
  methods: {
    // ======================================================
    // 初期化
    // ======================================================
    initialize: function (devices) {
      this.isDialog = true; // ダイアログの表示
      this.setDisplay(devices); // 画面の表示設定
    },
    // ======================================================
    // 表示設定
    // ======================================================
    setDisplay: function (devices) {
      this.isRunning = false; // 停止中

      this.devices.length = 0;
      for (const device of devices) {
        var item = {
          id: device.id,
          name: device.name,
          status: "ローディング待ち",
        };
        this.devices.unshift(item);
      }
    },
    // ======================================================
    // 全対象APIコール
    // ======================================================
    callLoaderAPI: function () {
      this.isRunning = true;
      // * ロード対象デバイスを取得する
      useGetSchedule(null)
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status != 0) {
            throw new Error(message);
          }
          this.doLoaderList(data);
        })
        .catch((error) => {
          //失敗時
          alert("全てのセンサーデータのロードに失敗しました。");
          console.log(error);
          this.isRunning = false;
        });
    },
    // ======================================================
    // 未ロード対象APIコール
    // ======================================================
    callLoaderSelected: function () {
      this.isRunning = true;
      // * ロード対象デバイスを取得する
      useGetSchedule(null)
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status != 0) {
            throw new Error(message);
          }
          let list = [];
          for (const device of data) {
            if ((device.dataStatus & 8) == 0) {
              list.push(device);
            }
          }
          this.doLoaderList(list);
        })
        .catch((error) => {
          //失敗時
          alert("センサーデータのロードに失敗しました。");
          console.log(error);
          this.isRunning = false;
        });
    },
    // ======================================================
    // 対象の作成とロードの実行
    // ======================================================
    doLoaderList: function (list) {
      this.count = list.length;
      for (const device of list) {
        this.callLoader(device.id);
      }
    },
    // ======================================================
    // ロードAPIコール
    // ======================================================
    callLoader: function (deviceId) {
      // * 表示の初期化
      this.changeStatus(deviceId, "ローディング中");
      // * ロードAPIをコール
      useLoadData(deviceId)
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if (status != 0) {
            throw new Error(message);
          }
          for (const item of data) {
            this.checkStatus(item.id, item.status);
          }
        })
        .catch((error) => {
          console.log(error);
          alert("デバイスID：" + deviceId + "のロードに失敗しました。");
        })
        .finally(() => {
          this.count--;
          if (0 == this.count) {
            alert("ローディングが完了しました。");
            this.isRunning = false;
          }
        });
    },
    // ======================================================
    // 実行結果の設定
    // ======================================================
    checkStatus(id, status) {
      if (status == -1) {
        this.changeStatus(id, "エラー");
      } else if ((status & 1) > 0) {
        this.changeStatus(id, "ロック中のため処理できませんでした。");
      } else if ((status & 8) > 0) {
        this.changeStatus(id, "完了");
      } else {
        this.changeStatus(
          id,
          "基礎データはロードされましたが、モデル作成はできませんでした。"
        );
      }
    },
    // ======================================================
    // 項目のステータス変更
    // ======================================================
    changeStatus(targetId, message) {
      for (const item of this.devices) {
        if (item.id == targetId) {
          item.status = message;
          break;
        }
      }
    },
  },
};
</script>}
<style lang="scss">
</style>
  