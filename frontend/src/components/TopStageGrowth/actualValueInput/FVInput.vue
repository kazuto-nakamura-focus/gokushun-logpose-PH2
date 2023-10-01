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
              <v-subheader class="ma-0 mt-n5 pa-0"><small>平均房重（ｇ）</small></v-subheader>
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
                  ref="date"
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
                  ref="date"
                  width="100%"
                  @onChange="handlElDate"
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
                  ref="date"
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
      <v-divider class="divider_top" />
      <v-row>
        <v-col cols="3">
          <v-subheader>着果負担 </v-subheader>
        </v-col>
        <v-col cols="2">
          <v-subheader>{{ fruitValues.burden }} </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_center" />
      <v-row>
        <v-col cols="3">
          <v-subheader>積算推定樹冠光合成量あたりの着果量 </v-subheader>
        </v-col>
        <v-col cols="2">
          <v-subheader>{{ fruitValues.amount }} </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_center" />
      <v-row>
        <v-col cols="3">
          <v-subheader>実測着果数/樹冠葉面積 </v-subheader>
        </v-col>
        <v-col cols="2">
          <v-subheader>{{ fruitValues.count }} </v-subheader>
        </v-col>
      </v-row>
      <v-divider class="divider_bottom" />
    </v-card>
  </v-container>
</template>

<script>
import {
  useFruitValues,
  useFruitValueSproutTreatment,
  useFruitValueELStage,
  useFruitValueBagging,
} from "@/api/TopStateGrowth/index";
import Ph2DatePicker from "@/components/parts/Ph2DatePicker.vue";

// const fruitValuesDTOData = {
//   burden: 0.5,	//* 着果負担
//   amount: 0.8, //* 積算推定樹冠光合成量あたりの着果量（g/mol
//   count: 0.4, //* 実測着果数/樹冠葉面積（房数/㎠）
// };

export default {
  props: {
    shared /** MountController */: { required: true },
    selectedMenu: Object,
    selectedField: Array,
    selectedYears: Array,
    selectedDevices: Array,
  },

  data() {
    return {
      fieldId: null, // 選ばれた圃場ID
      devicesId: this.$store.getters.selectedDevice.id, // 選ばれたデバイスID
      year: this.$store.getters.selectedYear.id, //年度

      fruitValues: {
        burden: null, //* 着果負担
        amount: null, //* 積算推定樹冠光合成量あたりの着果量（g/mol
        count: null, //* 実測着果数/樹冠葉面積（房数/㎠）
      },
      //着生後芽かき処理時
      fruitValueSproutTreatment: {
        date: null, //実測日
        weight: null, //平均房重
        count: null, //実測着果数
      },
      //E-L 27～31の生育ステージ時
      fruitValueELStage: {
        date: null, //実測日
        weight: null, //平均房重
        count: null, //実測着果数
      },
      //袋かけ時
      fruitValueBagging: {
        date: null, //実測日
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
  mounted() {
    this.shared.mount(this);
    this.getUseFruitValues();
  },
  methods: {
    initialize: function (data) {
    this.$nextTick(
        function () {
          this.$refs.date.initialize(data.menu.selectedYear);
        }.bind(this)
      );
    },
    handleSproutDate(date){
      this.fruitValueSproutTreatment.date = date;
    },
    handleElDate(date){
      this.fruitValueELStage.date = date;
    },
    handleBaggageDate(date){
      this.fruitValueBagging.date = date;
    },
    getUseFruitValues() {
      //圃場着果量着果負担詳細取得
      useFruitValues(this.devicesId, this.year)
        .then((response) => {
          //成功時
          const results = response["data"];
          console.log(this.devicesId);
          console.log(this.year);
          this.fruitValues = results.data;
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
        eventId: null,
        ...this.fruitValueSproutTreatment,
      };
      console.log("saveUseFruitValueSproutTreatment", data);
      //着生後芽かき処理時実績値更新
      useFruitValueSproutTreatment(data)
        .then((response) => {
          const results = response["data"];
          console.log("results", results);
          this.getUseFruitValues();
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    saveUseFruitValueELStage: function () {
      const data = {
        deviceId: parseInt(this.devicesId),
        year: this.year,
        eventId: null,
        ...this.fruitValueELStage,
      };
      ////E-L 27～31の生育ステージ時実績値更新処理
      useFruitValueELStage(data)
        .then((response) => {
          const results = response["data"];
          console.log("results", results);
          this.getUseFruitValues();
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    saveUseFruitValueBagging: function () {
      const data = {
        deviceId: parseInt(this.devicesId),
        year: this.year,
        eventId: null,
        ...this.fruitValueBagging,
      };
      //袋かけ時実績値更新処理
      useFruitValueBagging(data)
        .then((response) => {
          const results = response["data"];
          console.log("results", results);
          this.getUseFruitValues();
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //圃場、年度変更し処理
    updateTable() {
      this.getUseFruitValues();
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
