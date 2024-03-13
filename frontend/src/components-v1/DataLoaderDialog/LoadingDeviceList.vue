<template>
  <v-app>
    <v-dialog v-model="isDialog" width="60%" persistent>
      <v-card>
        <v-card-title>全センサーデータのロード</v-card-title>

        <v-card-text>
          <v-data-table :headers="headers" :items="devices" dense elevation-6></v-data-table>
        </v-card-text>

        <v-card-actions>
          <v-btn
            color="primary"
            class="ma-2 white--text"
            elevation="2"
            @click="confirmAction()"
            :disabled="isRunning == true"
          >全ての再ロードを実行する</v-btn>

          <v-btn
            color="primary"
            class="ma-2 white--text"
            elevation="2"
            @click="callLoaderSelected()"
            :disabled="isRunning == true"
          >再ロード(未完了のみ)</v-btn>

          <v-btn elevation="2" @click="isDialog = false" :disabled="isRunning == true">閉じる</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <confirmDailog :shared="sharedConfirm" ref="confirm" />
  </v-app>
</template>
  
<script>
import {
  useGetSchedule,
  useLoadData,
  useGetAllDataLoadStatus,
} from "@/api/ManagementScreenTop/MSDevice";
import moment from "moment";
import { DialogController } from "@/lib/mountController.js";
import confirmDailog from "@/components-v1/common/dialog/confirmDialog.vue";

const HEADERS = [
  { text: "デバイス名", value: "name", sortable: true, width: "40%" },
  { text: "ID", value: "id", sortable: true, width: "10%" },
  { text: "ステータス", value: "status", sortable: true, width: "50%" },
];

export default {
  props: { shared /** DialogController */: { required: true } },

  data() {
    return {
      sharedConfirm: new DialogController(),

      headers: HEADERS,
      devices: [],
      isDialog: false,
      isRunning: false,
      now: null,
      count: 0,
    };
  },
  components: {
    confirmDailog,
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
    // 確認画面の表示と実行
    // ======================================================
    confirmAction() {
      this.sharedConfirm.setUp(
        this.$refs.confirm,
        function (confirm) {
          confirm.initialize(
            "全てのセンサーデータが変更されますが、よろしいですか？処理時間は10分ほどかかります。",
            "はい",
            "いいえ"
          );
        },
        function (action) {
          if (action) {
            this.callLoaderAPI();
          }
        }.bind(this)
      );
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
          this.now = moment().format("YYYY-MM-DD");
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
          this.now = moment().format("YYYY-MM-DD");
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
      this.isRunning = true;
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
          console.log(response);
          this.count--;
          if (this.count == 0) {
            this.checkDataStatus();
          }
        })
        .catch((error) => {
          console.log(error);
          this.count--;
          if (this.count == 0) {
            this.checkDataStatus();
          }
        });
    },

    // ======================================================
    // 定期的にステータスのチェックを行う
    // ======================================================
    checkDataStatus: function () {
      this.intervalid1 = setInterval(
        function () {
          // ステータスチェックAPIのコールとステータス設定
          this.callApiGetAllDataLoadStatus(this.now);
          // 結果isRunningがfalseなら監視の終了
          if (!this.isRunning) {
            clearInterval(this.intervalid1);
          }
        }.bind(this),
        3000
      );
    },
    // ======================================================
    // ステータスのチェックを行うAPIのコールと処理
    // ======================================================
    callApiGetAllDataLoadStatus(startTime) {
      useGetAllDataLoadStatus(startTime)
        .then((response) => {
          const { status, message, data } = response["data"];
          let nowobj = moment(this.now);
          if (status != 0) {
            throw new Error(message);
          }
          let isRunning = false;
          for (const item of data) {
            if ((status & 1) > 0) {
              isRunning = true;
              this.changeStatus(item.id, "ローディング中");
            } else {
              if (null == item.date) {
                this.changeStatus(
                  item.id,
                  "実行中に処理を中断しました。再ロードを試みてください。"
                );
              } else {
                let statusTime = moment(item.date);
                if (statusTime.isBefore(nowobj)) continue;
                this.changeStatus(item.id, this.checkStatus(item.status));
              }
            }
          }
          this.isRunning = isRunning;
        })
        .catch((error) => {
          //失敗時
          alert("状態の確認ができませんでした。");
          console.log(error);
          this.isRunning = false;
        });
    },
    // ======================================================
    // 実行結果の設定
    // ======================================================
    checkStatus(status) {
      if (status == -1) {
        return "エラー";
      } else if ((status & 8) > 0) {
        return "完了";
      } else {
        return "基礎データはロードされましたが、モデル作成はできませんでした。";
      }
    },
    // ======================================================
    // 項目のステータス変更
    //  与えられたデバイスIDに合わせて表示デバイスのメッセージを
    //  設定する
    // ======================================================
    changeStatus(deviceId, message) {
      for (const item of this.devices) {
        if (item.id == deviceId) {
          item.status = message;
          break;
        }
      }
    },
  },
};
</script>
<style lang="scss">
</style>
  