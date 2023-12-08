<!--着果量着果負担表示画面-->
<template>
  <v-container dense>
    <v-card elevation="0" class="ma-1">
      <v-subheader>実測値入力</v-subheader>
      <v-divider class="divider_center"></v-divider>
      <!-- 入力部分 -->
      <v-row>
        <v-col cols="10">
          <v-row>
            <v-col cols="3">
              <v-subheader class="ma-0 mt-n5 pa-0"> </v-subheader>
            </v-col>
            <v-col cols="3">
              <v-subheader class="ma-0 mt-n5 pa-0">実測日</v-subheader>
            </v-col>
            <v-col cols="2">
              <v-subheader class="ma-0 mt-n5 pa-0"
                ><small>平均房重（ｇ）</small></v-subheader
              >
            </v-col>
            <v-col cols="2">
              <v-subheader class="ma-0 mt-n5 pa-0">実測着果数</v-subheader>
            </v-col>
            <v-col cols="2">
              <v-subheader class="ma-0 mt-n5 pa-0"> </v-subheader>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="10">
          <v-row>
            <v-col cols="3">
              <div class="ma-0 mt-n5 pa-0 pl-4">着生後芽かき処理時</div>
            </v-col>
            <v-col cols="3">
              <div style="margin-top: -40px; padding: 0">
                <ph-2-date-picker
                  ref="date1"
                  width="100%"
                  @onChange="handleSproutDate"
                  style="margin: 0; padding: 0"
                  dense
                />
              </div>
            </v-col>
            <v-col cols="2">
              <v-text-field
                class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
                dense
                hide-details="auto"
                outlined
                v-model.number="fruitValueSproutTreatment.weight"
              ></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-text-field
                class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
                dense
                hide-details="auto"
                outlined
                v-model.number="fruitValueSproutTreatment.count"
              ></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-btn
                class="primary ma-0 mt-n12 pl-1 pr-1"
                @click="saveUseFruitValueSproutTreatment"
                >保存</v-btn
              >
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="10">
          <v-row>
            <v-col cols="3">
              <div class="ma-0 mt-n5 pa-0 pl-4">E-L 27～31の生育ステージ時</div>
            </v-col>
            <v-col cols="3">
              <div style="margin-top: -40px; padding: 0">
                <ph-2-date-picker
                  ref="date2"
                  width="100%"
                  @onChange="handleElDate"
                  style="margin: 0; padding: 0"
                  dense
                />
              </div>
            </v-col>
            <v-col cols="2">
              <v-text-field
                class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
                dense
                hide-details="auto"
                outlined
                v-model.number="fruitValueELStage.weight"
              ></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-text-field
                class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
                dense
                hide-details="auto"
                outlined
                v-model.number="fruitValueELStage.count"
              ></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-btn
                class="primary ma-0 mt-n12 pl-1 pr-1"
                @click="saveUseFruitValueELStage"
                >保存</v-btn
              >
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="10">
          <v-row>
            <v-col cols="3">
              <div class="ma-0 mt-n5 pa-0 pl-4">袋かけ時</div>
            </v-col>
            <v-col cols="3">
              <div style="margin-top: -40px; padding: 0">
                <ph-2-date-picker
                  ref="date3"
                  width="100%"
                  @onChange="handleBaggageDate"
                  style="margin: 0; padding: 0"
                  dense
                />
              </div>
            </v-col>
            <v-col cols="2">
              <v-text-field
                class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
                dense
                hide-details="auto"
                outlined
                v-model.number="fruitValueBagging.weight"
              ></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-text-field
                class="ma-0 mt-n8 pl-1 pr-1 text_field_size"
                dense
                hide-details="auto"
                outlined
                v-model.number="fruitValueBagging.count"
              ></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-btn
                class="primary ma-0 mt-n12 pl-1 pr-1"
                @click="saveUseFruitValueBagging"
                >保存</v-btn
              >
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <!-- デバイスタイトル -->
      <v-row>
        <v-col cols="3">
          <v-subheader></v-subheader>
        </v-col>
        <v-col v-for="title in titles" cols="2" :key="title">
          <v-subheader>{{ title }} </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_top" />
      <v-row>
        <v-col cols="3">
          <v-subheader>着果負担 </v-subheader>
        </v-col>
        <v-col v-for="burden in burdens" cols="2" :key="burden">
          <v-subheader>{{ burden }} </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_center" />
      <v-row>
        <v-col cols="3">
          <v-subheader>積算推定樹冠光合成量あたりの着果量 </v-subheader>
        </v-col>
        <v-col v-for="amount in amounts" cols="2" :key="amount">
          <v-subheader>{{ amount }} </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_center" />
      <v-row>
        <v-col cols="3">
          <v-subheader>実測着果数/樹冠葉面積 </v-subheader>
        </v-col>
        <v-col v-for="count in counts" cols="2" :key="count">
          <v-subheader>{{ count }} </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_bottom" />
    </v-card>
  </v-container>
</template>

<script>
import {
  useFruitValue,
  useFruitValues,
  useFruitValueUpdate,
} from "@/api/TopStateGrowth/index";
import Ph2DatePicker from "@/components/parts/Ph2DatePicker.vue";
import moment from "moment";
// const fruitValuesDTOData = {
//   burden: 0.5,	//* 着果負担
//   amount: 0.8, //* 積算推定樹冠光合成量あたりの着果量（g/mol
//   count: 0.4, //* 実測着果数/樹冠葉面積（房数/㎠）
// };

export default {
  props: {
    selectedMenu: Object,
    selectedField: Array,
    selectedYears: Array,
    selectedDevices: Array,
  },

  data() {
    return {
      devicesId: null, // 選ばれたデバイスID
      year: null, //年度
      deviceName: null,
      baseDeviceId: 53,
      ids: [],
      titles: [],
      burdens: [],
      amounts: [],
      counts: [],

      fruitValues: {
        burden: null, //* 着果負担
        amount: null, //* 積算推定樹冠光合成量あたりの着果量（g/mol
        count: null, //* 実測着果数/樹冠葉面積（房数/㎠）
      },
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

      //カレンダー選択ダイアログステータス
      menu1: false,
      menu2: false,
      menu3: false,
    };
  },
  components: {
    Ph2DatePicker,
  },
  mounted() {},
  methods: {
    initialize: function (menu) {
      this.devicesId = menu.selectedDevice.id;
      this.year = menu.selectedYear.id;
      this.deviceName = menu.selectedDevice.name;
      // * 基準デバイスの場合、リストを初期化
      if (this.devicesId == this.baseDeviceId) {
        this.ids.length = 0;
        this.titles.length = 0;
        this.burdens.length = 0;
        this.amounts.length = 0;
        this.counts.length = 0;
      }
      // * 基準デバイスデータが無い場合 *//
      if (0 == this.ids.length) {
        this.getBaseUseFruitValues();
      } else {
        this.getUseFruitValues();
      }

      this.$nextTick(function () {
        this.$refs.date1.initialize(menu.selectedYear);
        this.$refs.date2.initialize(menu.selectedYear);
        this.$refs.date3.initialize(menu.selectedYear);
      });
    },
    //* ============================================
    // 着果量・着果負担整形
    //* ============================================
    formatFuitsValues(deviceId, deviceName, data) {
      let shortBurden = Math.round(data.burden * 100) / 100;
      if (shortBurden.isNaN) shortBurden = 0;
      let shortAmount = Math.round(data.amount * 100) / 100;
      if (shortAmount.isNaN) shortAmount = 0;
      let shortCount = Math.round(data.count * 100) / 100;
      if (shortCount.isNaN) shortCount = 0;
      let burdernString;
      let amountString;
      let countString;
      // 基準デバイスの場合
      if (deviceId == this.baseDeviceId) {
        burdernString = String(shortBurden);
        amountString = String(shortAmount);
        countString = String(shortCount);
      } else {
        // 比較デバイスの場合
        let diff = shortBurden - this.burdens[0];
        diff = Math.round(diff * 100) / 100;
        if (diff.isNaN) diff = 0;
        burdernString =
          diff > 0
            ? String(shortBurden) + "(+" + String(diff) + ")"
            : String(shortBurden) + "(" + String(diff) + ")";
        diff = shortAmount - this.amounts[0];
        diff = Math.round(diff * 100) / 100;
        if (diff.isNaN) diff = 0;
        amountString =
          diff > 0
            ? String(shortAmount) + "(+" + String(diff) + ")"
            : String(shortAmount) + "(" + String(diff) + ")";
        diff = shortCount - this.counts[0];
        diff = Math.round(diff * 100) / 100;
        if (diff.isNaN) diff = 0;
        countString =
          diff > 0
            ? String(shortCount) + "(+" + String(diff) + ")"
            : String(shortCount) + "(" + String(diff) + ")";
      }
      // 既に表示されているものがあれば、その値と交換し、無ければ追加する
      let inclueded = false;
      let index = 0;
      for (const id of this.ids) {
        if (id == deviceId) {
          inclueded = true;
          break;
        }
        index++;
      }
      if (!inclueded) {
        this.ids.push(deviceId);
        this.titles.push(deviceName);
        this.burdens.push(burdernString);
        this.amounts.push(amountString);
        this.counts.push(countString);
      } else if (index != 0) {
        this.burdens.splice(index, 1, burdernString);
        this.amounts.splice(index, 1, amountString);
        this.counts.splice(index, 1, countString);
      }
    },
    //* ============================================
    // 個々の着果負担値を取得し、フィールドに設定する
    //* ============================================
    getFruitValue(eventId, field) {
      useFruitValue(this.devicesId, field.date, eventId)
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            field.weight = null == data ? "未設定" : data.average;
            field.count = null == data ? "未設定" : data.count;
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          alert("詳細取得ができませんでした。");
          console.log(error);
        });
    },
    handleSproutDate(date) {
      if (null != date) this.fruitValueSproutTreatment.date = date;
      this.getFruitValue(1, this.fruitValueSproutTreatment);
    },
    handleElDate(date) {
      if (null != date) this.fruitValueELStage.date = date;
      this.getFruitValue(2, this.fruitValueELStage);
    },
    handleBaggageDate(date) {
      if (null != date) this.fruitValueBagging.date = date;
      this.getFruitValue(3, this.fruitValueBagging);
    },
    getBaseUseFruitValues() {
      //圃場着果量着果負担詳細取得
      useFruitValues(this.baseDeviceId, this.year)
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            this.formatFuitsValues(
              this.baseDeviceId,
              "葡萄専心イチノセ2本(新)",
              data
            );
            this.getUseFruitValues();
          } else {
            alert("詳細取得ができませんでした。");
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    getUseFruitValues() {
      //圃場着果量着果負担詳細取得
      useFruitValues(this.devicesId, this.year)
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            this.formatFuitsValues(this.devicesId, this.deviceName, data);
          } else {
            alert("詳細取得ができませんでした。");
            throw new Error(message);
          }
          console.log(this.devicesId);
          console.log(this.year);
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    saveUseFruitValueSproutTreatment: function () {
      const data = {
        deviceId: parseInt(this.devicesId),
        year: this.year,
        eventId: 1,
        ...this.fruitValueSproutTreatment,
      };
      //着生後芽かき処理時実績値更新
      this.updateFruitValue(data);
    },

    saveUseFruitValueELStage: function () {
      const data = {
        deviceId: parseInt(this.devicesId),
        year: this.year,
        eventId: 2,
        ...this.fruitValueELStage,
      };
      ////E-L 27～31の生育ステージ時実績値更新処理
      this.updateFruitValue(data);
    },

    saveUseFruitValueBagging: function () {
      const data = {
        deviceId: parseInt(this.devicesId),
        year: this.year,
        eventId: 3,
        ...this.fruitValueBagging,
      };
      this.updateFruitValue(data);
    },
    //圃場、年度変更し処理
    updateTable() {
      this.getUseFruitValues();
    },

    updateFruitValue(data) {
      //袋かけ時実績値更新処理
      useFruitValueUpdate(data)
        .then((response) => {
          const { status, message } = response["data"];
          if (status === 0) {
            alert("登録が完了しました。");
            this.getUseFruitValues();
          } else {
            alert("登録が失敗しました。");
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
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
