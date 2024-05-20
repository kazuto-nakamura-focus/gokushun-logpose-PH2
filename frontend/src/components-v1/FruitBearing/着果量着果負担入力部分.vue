<!--着果量着果負担表示画面-->
<template>
  <v-container dense>
    <v-card elevation="0" class="ma-1">
      <!-- 入力部分 -->
      <v-row>
        <v-col cols="3">
          <div class="ma-0 mt-n5 pa-0 pl-4">着生後芽かき処理時</div>
        </v-col>
        <v-col cols="3">
          <v-text-field
            label="実測日"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            background-color="#F4FCE0"
            @mousedown="showDatePicker('1')"
            v-model="fruitValueSproutTreatment.date"
          ></v-text-field>
        </v-col>
        <v-col cols="2">
          <v-text-field
            label="平均房重(g)"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            background-color="#F4FCE0"
            v-model.number="fruitValueSproutTreatment.weight"
          ></v-text-field>
        </v-col>
        <v-col cols="2">
          <v-text-field
            label="実測着果数"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            background-color="#F4FCE0"
            v-model.number="fruitValueSproutTreatment.count"
          ></v-text-field>
        </v-col>
        <v-col cols="1">
          <v-btn
            class="primary ma-0 mt-n12 pl-1 pr-1"
            @click="saveUseFruitValueSproutTreatment"
            >保存</v-btn
          >
        </v-col>
        <v-col cols="1">
          <v-btn class="primary ma-0 mt-n12 pl-1 pr-1" @click="deleteData(1)"
            >取消</v-btn
          >
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="3">
          <div class="ma-0 mt-n5 pa-0 pl-4">結実期</div>
        </v-col>
        <v-col cols="3">
          <v-text-field
            label="実測日"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            @mousedown="showDatePicker('2')"
            v-model="fruitValueELStage.date"
            background-color="#F4FCE0"
          ></v-text-field>
        </v-col>
        <v-col cols="2">
          <v-text-field
            label="平均房重(g)"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            v-model.number="fruitValueELStage.weight"
            background-color="#F4FCE0"
          ></v-text-field>
        </v-col>
        <v-col cols="2">
          <v-text-field
            label="実測着果数"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            v-model.number="fruitValueELStage.count"
            background-color="#F4FCE0"
          ></v-text-field>
        </v-col>
        <v-col cols="1">
          <v-btn
            class="primary ma-0 mt-n12 pl-1 pr-1"
            @click="saveUseFruitValueELStage"
            >保存</v-btn
          >
        </v-col>
        <v-col cols="1">
          <v-btn class="primary ma-0 mt-n12 pl-1 pr-1" @click="deleteData(2)"
            >取消</v-btn
          >
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="3">
          <div class="ma-0 mt-n5 pa-0 pl-4">袋かけ時</div>
        </v-col>
        <v-col cols="3">
          <v-text-field
            label="実測日"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            @mousedown="showDatePicker('3')"
            v-model="fruitValueBagging.date"
            background-color="#F4FCE0"
          ></v-text-field>
        </v-col>
        <v-col cols="2">
          <v-text-field
            label="平均房重(g)"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            v-model.number="fruitValueBagging.weight"
            background-color="#F4FCE0"
          ></v-text-field>
        </v-col>
        <v-col cols="2">
          <v-text-field
            label="実測着果数"
            class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
            dense
            hide-details="auto"
            outlined
            v-model.number="fruitValueBagging.count"
            background-color="#F4FCE0"
          ></v-text-field>
        </v-col>
        <v-col cols="1">
          <v-btn
            class="primary ma-0 mt-n12 pl-1 pr-1"
            @click="saveUseFruitValueBagging"
            >保存</v-btn
          >
        </v-col>
        <v-col cols="1">
          <v-btn class="primary ma-0 mt-n12 pl-1 pr-1" @click="deleteData(3)"
            >取消</v-btn
          >
        </v-col>
      </v-row>
    </v-card>
    <v-dialog v-model="isDateDialog" width="auto">
      <v-date-picker
        v-model="dateInfo.date"
        :min="dateInfo.minDate"
        :max="dateInfo.maxDate"
        @change="hideDatePicker"
        locale="jp-ja"
      ></v-date-picker>
    </v-dialog>
  </v-container>
</template>

<script>
import {
  useFruitValue,
  useFruitValueUpdate,
  useFruitValueDelete,
} from "@/api/TopStateGrowth/index";
import moment from "moment";

export default {
  props: {
    selectedMenu: Object,
    selectedField: Array,
    selectedYears: Array,
    selectedDevices: Array,
  },

  data() {
    return {
      isDateDialog: false,
      devicesId: null, // 選ばれたデバイスID
      year: null, //年度

      //着生後芽かき処理時
      fruitValueSproutTreatment: {
        date: moment().format("YYYY-MM-DD"), //実測日
        weight: null, //平均房重
        count: null, //実測着果数
      },
      //E-L 27～31の生育ステージ時
      fruitValueELStage: {
        date: moment().format("YYYY-MM-DD"), //実測日
        weight: null, //平均房重
        count: null, //実測着果数
      },
      //袋かけ時
      fruitValueBagging: {
        date: moment().format("YYYY-MM-DD"), //実測日
        weight: null, //平均房重
        count: null, //実測着果数
      },

      dateInfo: {
        date: null,
        minDate: null,
        maxDate: null,
        eventId: null,
      },
    };
  },
  components: {},
  mounted() {
    this.$emit("mounted");
  },
  methods: {
    //* ============================================
    // 入力フィールドの初期化を行う
    //* ============================================
    setEmptyData(date) {
      this.fruitValueSproutTreatment = {
        date: date, //実測日
        weight: null, //平均房重
        count: null, //実測着果数
      };
      //E-L 27～31の生育ステージ時
      this.fruitValueELStage = {
        date: date, //実測日
        weight: null, //平均房重
        count: null, //実測着果数
      };
      //袋かけ時
      this.fruitValueBagging = {
        date: date, //実測日
        weight: null, //平均房重
        count: null, //実測着果数
      };
    },
    initialize: function (menu) {
      this.devicesId = menu.selectedDevice.id;
      this.year = menu.selectedYear.id;
      this.getFruitValue(this.devicesId, this.year);
    },
    //* ============================================
    // 個々の着果負担値を取得し、フィールドに設定する
    //* ============================================
    getFruitValue(device, year) {
      useFruitValue(device, year)
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            this.dateInfo.minDate = data.startDate;
            this.dateInfo.maxDate = data.endDate;
            let today = moment().format("YYYY-MM-DD");
            if (this.dateInfo.maxDate > today) {
              this.setEmptyData(today);
            } else {
              this.setEmptyData(data.endDate);
            }
            for (const item of data.values) {
              if (item.eventId == 1) {
                this.fruitValueSproutTreatment.date = item.targetDate;
                this.fruitValueSproutTreatment.weight = item.average;
                this.fruitValueSproutTreatment.count = item.count;
              } else if (item.eventId == 2) {
                this.fruitValueELStage.date = item.targetDate;
                this.fruitValueELStage.weight = item.average;
                this.fruitValueELStage.count = item.count;
              } else if (item.eventId == 3) {
                this.fruitValueBagging.date = item.targetDate;
                this.fruitValueBagging.weight = item.average;
                this.fruitValueBagging.count = item.count;
              }
            }
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          alert("詳細取得ができませんでした。");
          console.log(error);
        });
    },

    //* ============================================
    // 着生後芽かき処理時
    //* ============================================
    saveUseFruitValueSproutTreatment: function () {
      const data = {
        deviceId: parseInt(this.devicesId),
        year: this.year,
        eventId: 1,
        ...this.fruitValueSproutTreatment,
      };
      this.updateFruitValue(data);
    },
    //* ============================================
    // 結実期データを保存する
    //* ============================================
    saveUseFruitValueELStage: function () {
      const data = {
        deviceId: parseInt(this.devicesId),
        year: this.year,
        eventId: 2,
        ...this.fruitValueELStage,
      };
      this.updateFruitValue(data);
    },
    //* ============================================
    // 袋かけ時データを保存する
    //* ============================================
    saveUseFruitValueBagging: function () {
      const data = {
        deviceId: parseInt(this.devicesId),
        year: this.year,
        eventId: 3,
        ...this.fruitValueBagging,
      };
      this.updateFruitValue(data);
    },
    //* ============================================
    // 実績値の更新APIをコールする
    //* ============================================
    updateFruitValue(data) {
      //袋かけ時実績値更新処理
      useFruitValueUpdate(data)
        .then((response) => {
          const { status, message } = response["data"];
          if (status === 0) {
            alert("登録が完了しました。");
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          alert("登録が失敗しました。");
          console.log(error);
        });
    },
    //* ============================================
    // 日付入力を表示
    //* ============================================
    showDatePicker(eventId) {
      //* 日付入力を表示
      this.dateInfo.eventId = eventId;
      if (eventId == 1) {
        this.dateInfo.date = this.fruitValueSproutTreatment.date;
      } else if (eventId == 2) {
        this.dateInfo.date = this.fruitValueELStage.date;
      } else if (eventId == 3) {
        this.dateInfo.date = this.fruitValueBagging.date;
      }
      this.isDateDialog = true;
    },
    //* ============================================
    // 日付を隠す
    //* ============================================
    hideDatePicker() {
      this.isDateDialog = false;
      if (this.dateInfo.eventId == 1) {
        this.fruitValueSproutTreatment.date = this.dateInfo.date;
      } else if (this.dateInfo.eventId == 2) {
        this.fruitValueELStage.date = this.dateInfo.date;
      } else if (this.dateInfo.eventId == 3) {
        this.fruitValueBagging.date = this.dateInfo.date;
      }
    },
    //* ============================================
    // 実測値をクリアする
    //* ============================================
    deleteData(eventId) {
      //袋かけ時実績値更新処理
      useFruitValueDelete(this.devicesId, this.year, eventId)
        .then((response) => {
          const { status, message } = response["data"];
          if (status === 0) {
            alert("値を取消ました。");
            if (eventId == 1) {
              this.fruitValueSproutTreatment.weight = null;
              this.fruitValueSproutTreatment.count = null;
            } else if (eventId == 2) {
              this.fruitValueELStage.weight = null;
              this.fruitValueELStage.count = null;
            } else if (eventId == 3) {
              this.fruitValueBagging.weight = null;
              this.fruitValueBagging.count = null;
            }
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          alert("取消に失敗しました。");
          console.log(error);
        });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/style/common.css";

.divider_top {
  margin-bottom: 10px;
  color: "black";
}

.divider_center {
  margin-top: 10px;
  margin-bottom: 10px;
  color: "black";
}

.divider_bottom {
  margin-top: 10px;
  color: "black";
}

.text_field_size {
  min-width: 60px;
}
</style>
