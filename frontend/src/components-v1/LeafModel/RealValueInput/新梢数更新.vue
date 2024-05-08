<!--新梢数入力-->
<template>
  <v-container>
    <div class="text-subtitle-1">新梢数</div>
    <v-subheader class="ma-0 mt-n3 pa-0">
      (芽搔き直後に実施してください)
      センサーを装着している樹をサンプルとしてカウントしてください
    </v-subheader>

    <v-container class="sprout">
      <v-row>
        <v-col cols="4">
          <v-text-field
            label="実測日【必須】"
            dense
            hide-details="auto"
            outlined
            filled
            background-color="#F4FCE0"
            v-model.trim="serverData.date"
            @focus="openDateInput"
          ></v-text-field>
        </v-col>

        <v-col cols="4">
          <v-text-field
            label="新梢数【必須】"
            dense
            hide-details="auto"
            outlined
            filled
            background-color="#F4FCE0"
            v-model.trim="serverData.count"
          ></v-text-field>
          <p v-if="!isCountInput" class="error">{{this.messages.numeric}}</p>
        </v-col>
        <v-col cols="4">
          <div>
            <v-btn color="primary" elevation="2" :disabled="buttonStatus != 0" @click="saveNew()">保存</v-btn>
          </div>
        </v-col>
      </v-row>
    </v-container>
    <div class="datePicker" ref="dateInput">
      <v-date-picker
        v-model="dateInfo.date"
        :min="dateInfo.minDate"
        :max="dateInfo.maxDate"
        @change="handleChangeDate"
        locale="jp-ja"
      ></v-date-picker>
    </div>
  </v-container>
</template>
<script>
import {
  useLeafValueShootDetail,
  useLeafValueShoot,
} from "@/api/TopStateGrowth/LAActualValueInput";
import messages from "@/assets/messages.json";

var regexp = new RegExp(/^([1-9]\d*|0)(\.\d+)?$/);

export default {
  props: {
    /*
    target {deviceId, year}
    */
    target: Object,
  },

  data() {
    return {
      messages: messages,

      buttonStatus: false,
      // 日付データ
      dateInfo: {
        date: null,
        minDate: null,
        maxDate: null,
        params: null,
      },
      // ユーザーデータ
      deviceId: this.target.deviceId,
      year: this.target.year,
      serverData: { count: "", date: "" },
      // 更新されたかどうかのフラグ
      isUpdated: false,
    };
  },

  mounted() {
    this.callAPILeafValueShootDetail();
  },
  //* ============================================
  // 入力チェック
  //* ============================================
  computed: {
    isCountInput() {
      if (this.serverData.count.length == 0) {
        // * データの入力が無い場合、エラーメッセージは出さないが、ボタンは無効化にする
        this.setStatus(1, false);
        return false;
      } else {
        // * データの入力がある場合、数値形式でない場合、エラーメッセージを表示し、ボタン無効化する
        let isNumber = regexp.test(this.serverData.count);
        this.setStatus(1, isNumber);
        return isNumber;
      }
    },
  },
  methods: {
    //* ============================================
    // 入力チェックとボタン表示の設定
    //* ============================================
    setStatus(status, bool) {
      if (!bool) {
        this.buttonStatus = this.buttonStatus | status;
      } else {
        this.buttonStatus = (this.buttonStatus | status) - status;
      }
      return bool;
    },
    //* ============================================
    // サーバーデータの取得
    //* ============================================
    callAPILeafValueShootDetail() {
      useLeafValueShootDetail(this.deviceId, this.year)
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status === 0) {
            /*
	        private Date startDate;
	        private Date endDate;
	        private String date;
	        private Integer count;
            */
            this.dateInfo.minDate = data.startDate;
            this.dateInfo.maxDate = data.endDate;
            this.serverData = data;
          } else {
            alert("新梢数取得に失敗しました。");
            throw new Error(message);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    //* ============================================
    // 日付表示
    //* ============================================
    openDateInput() {
      this.$refs.dateInput.style.display = "inline-block";
    },
    //* ============================================
    // 日付設定処理
    //* ============================================
    handleChangeDate() {
      this.$refs.dateInput.style.display = "none";
      this.serverData.date = this.dateInfo.date;
    },
    // ======================================================
    // データの登録を行う
    // ======================================================
    saveNew() {
      const data = {
        deviceId: this.deviceId,
        year: this.year,
        value: this.serverData,
      };
      //新梢数の登録
      useLeafValueShoot(data)
        .then((response) => {
          const { status, message } = response["data"];
          if (status === 0) {
            alert("登録が完了しました。");
            this.isUpdated = true;
          } else {
            alert("登録が失敗しました。");
            throw new Error(message);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
  },
};
</script>
  <style>
.v-application .mt-n12 {
  margin-top: 0;
}
</style>
  