<!--葉面積実績値入力-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="900">
        <v-card>
          <v-card-title v-if="title != null">実績値入力</v-card-title>
          <!-- タイトル部分 -->
          <input-header ref="titleHeader" />
          <!-- 入力部分 -->
          <v-container>
            <div class="text-subtitle-1">新梢数</div>
            <v-subheader class="ma-0 mt-n3 pa-0">
              (芽掻きのタイミングで実施)
              センサーを装着している樹をサンプルとしてカウントしてください
            </v-subheader>
            <div>
              <v-container class="sprout">
                <v-row>
                  <v-col cols="9">
                    <v-row>
                      <v-col cols="4">
                        <v-subheader class="ma-0 mt-n5 pa-0"
                          >実測日</v-subheader
                        >
                      </v-col>
                      <v-col cols="4">
                        <v-subheader class="ma-0 mt-n5 pa-0"
                          >新梢数</v-subheader
                        >
                      </v-col>
                      <v-col cols="4"></v-col>
                    </v-row>
                  </v-col>
                  <v-col cols="3"></v-col>
                </v-row>
                <v-row>
                  <v-col cols="9">
                    <v-row>
                      <v-col cols="4">
                        <ph-2-date-picker
                        ref="countDate"
                        width="100%"
                        @onChange="handleCountDate"
                        class="ma-0 mt-n8 pl-1 pr-1"
                      />
                      </v-col>
                      <v-col cols="4">
                        <v-text-field
                          class="ma-0 mt-n8 pl-1 pr-1"
                          dense
                          hide-details="auto"
                          outlined
                          v-model.number="leafAreaValueData.leafAreaNValue"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="4">
                        <v-btn
                          color="primary"
                          class="ma-0 mt-n12 pl-1 pr-1 white--text"
                          elevation="2"
                          @click="saveNew()"
                          >保存</v-btn
                        >
                      </v-col>
                    </v-row>
                  </v-col>
                  <v-col cols="3"></v-col>
                </v-row>
              </v-container>
            </div>

            <div class="text-subtitle-1 mt-5">葉面積・葉枚数</div>
            <v-subheader class="ma-0 mt-n3 pa-0">
              (測定タイミング : )
            </v-subheader>
            <div>
              <v-container
                class="sprout"
                v-for="leafAreaAreaData in leafAreaValueData.leafAreaAreaDataList"
                :key="leafAreaAreaData.id"
              >
                <v-row>
                  <v-col cols="7">
                    <v-row>
                      <v-col cols="4">
                        <v-subheader class="ma-0 mt-n5 pa-0"
                          >実測日</v-subheader
                        >
                      </v-col>
                      <v-col cols="4">
                        <v-subheader class="ma-0 mt-n5 pa-0"
                          >新梢あたり葉枚数</v-subheader
                        >
                      </v-col>
                      <v-col cols="4">
                        <v-subheader class="ma-0 mt-n5 pa-0"
                          >平均個葉面積</v-subheader
                        >
                      </v-col>
                    </v-row>
                  </v-col>
                  <v-col cols="1"></v-col>
                  <v-col cols="4">
                    <v-row>
                      <v-col cols="12">
                        <v-subheader class="ma-0 mt-n5 pa-0"
                          >実測樹幹葉面積</v-subheader
                        >
                      </v-col>
                    </v-row>
                  </v-col>
                </v-row>
                <v-row>
                  <v-col cols="7">
                    <v-row>
                      <v-col cols="4">
                        <ph-2-date-picker
                        ref="areaDate"
                        width="100%"
                        @onChange="handleAreaDate"
                        style="margin: 0; padding: 0"
                      />
                        <!-- <v-text-field class="ma-0 mt-n8 pl-1 pr-1" dense hide-details="auto" outlined
                            v-model.number="leafAreaAreaData.leafAreaADate"></v-text-field> -->
                      </v-col>
                      <v-col cols="4">
                        <v-text-field
                          class="ma-0 mt-n8 pl-1 pr-1"
                          dense
                          hide-details="auto"
                          outlined
                          v-model.number="leafAreaAreaData.leafAreaAValue"
                          @change="onChangeValue(leafAreaAreaData.id)"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="4">
                        <v-text-field
                          class="ma-0 mt-n8 pl-1 pr-1"
                          dense
                          hide-details="auto"
                          outlined
                          v-model.number="
                            leafAreaAreaData.leafAreaAValueAverage
                          "
                          @change="onChangeValue(leafAreaAreaData.id)"
                        ></v-text-field>
                      </v-col>
                    </v-row>
                  </v-col>
                  <v-col cols="1"></v-col>
                  <v-col cols="4">
                    <v-row>
                      <v-col cols="6">
                        <v-text-field
                          class="ma-0 mt-n8 pl-1 pr-1"
                          dense
                          hide-details="auto"
                          outlined
                          suffix="㎠"
                          filled
                          readonly
                          v-model.number="leafAreaAreaData.leafAreaAValueTotal"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="6">
                        <v-btn
                          color="primary"
                          class="ma-0 mt-n12 pl-1 pr-1 white--text"
                          elevation="2"
                          @click="saveArea(leafAreaAreaData.id)"
                          >保存</v-btn
                        >
                      </v-col>
                    </v-row>
                  </v-col>
                </v-row>
              </v-container>

              <v-container>
                <v-row>
                  <v-col cols="10"></v-col>
                  <v-col cols="2">
                    <v-btn
                      color="primary"
                      class="ma-2 white--text"
                      elevation="2"
                      fab
                      @click="addrow()"
                    >
                      <v-icon>mdi-plus</v-icon>
                    </v-btn>
                  </v-col>
                </v-row>
              </v-container>
            </div>
          </v-container>

          <div class="GS_ButtonArea">
            <!-- <v-btn color="primary" class="ma-2 white--text" elevation="2" @click="save()">保存</v-btn> -->
            <v-btn
              color="gray"
              class="ma-2 black--text"
              elevation="2"
              @click="close()"
              >キャンセル</v-btn
            >
          </div>
        </v-card>
      </v-dialog>
    </v-container>
  </v-app>
</template>
<script>
import moment from "moment";
import { mdiExitToApp } from "@mdi/js";
import InputHeader from "./InputHeader.vue";
import Ph2DatePicker from "@/components/parts/Ph2DatePicker.vue";

import {
  useLeafValueShootDetail,
  useLeafValueAreaAndCountDetail,
  useLeafValueShoot,
  useLeafValueAreaAndCount,
} from "@/api/TopStateGrowth/LAActualValueInput";

export default {
  name: "LAActualValueInput",
  props: {
    shared /** MountController */: { required: true },
    selectedField: Array,
    selectedDevices: Array,
  },

  data() {
    return {
      value: "",
      date: moment().format("YYYY-MM-DD"),
      isDialog: false,
      title: "", // 選ばれたモデル種別
      field: {}, // 選ばれた圃場
      year: 0,
      // selected: null,
      device: {},
      // params: [],
      path: mdiExitToApp,
      menu1: false,
      menu2: false,

      // headers: HEADERS,

      leafAreaValueData: {
        leafAreaNDate: moment().format("YYYY-MM-DD"),
        leafAreaNValue: 0,
        leafAreaAreaDataList: [
          {
            id: 0,
            leafAreaADate: moment().format("YYYY-MM-DD"),
            leafAreaAValue: 0,
            leafAreaAValueAverage: 0,
            leafAreaAValueTotal: 0,
          },
        ],
      },
    };
  },

  components: {
    Ph2DatePicker,
    InputHeader,
  },

  mounted() {
    this.shared.mount(this);
  },
  methods: {
    initialize: function (data) {
      this.$nextTick(
        function () {
          this.$refs.areaDate.initialize(data.menu.selectedYear);
          this.$refs.countDate.initialize(data.menu.selectedYear);
          this.$refs.titleHeader.initialize(data.menu);
        }.bind(this)
      );
      //年度
      this.year = this.$store.getters.selectedYear.id;
      // タイトル
      this.title = data.title;
      // 圃場
      this.field = {
        id: this.$store.getters.selectedField.id,
        name: this.$store.getters.selectedField.name,
      };
      // デバイス
      this.device = {
        id: this.$store.getters.selectedDevice.id,
        name: this.$store.getters.selectedDevice.name,
      };
      //this.isDialog = true;

      //新梢数取得処理
      useLeafValueShootDetail(this.device.id, this.date)
        .then((shoot_response) => {
          console.log(shoot_response);
          const la_data = shoot_response["data"]["data"];
          this.leafAreaValueData.leafAreaNDate = la_data.date;
          this.leafAreaValueData.leafAreaNValue = la_data.count;
          //新梢辺り葉枚数・平均個葉面積検索処理
          useLeafValueAreaAndCountDetail(this.device.id, this.date)
            .then((count_response) => {
              console.log(count_response);
              const la_count_data = count_response["data"]["data"];
              const data_list = [];
              const count_data = {
                id: 0,
                leafAreaADate: la_count_data.date,
                leafAreaAValue: la_count_data.count,
                leafAreaAValueAverage: la_count_data.averageArea,
                leafAreaAValueTotal: la_count_data.totalArea,
              };
              data_list.push(count_data);
              this.leafAreaValueData.leafAreaAreaDataList = data_list;
            })
            .catch((error) => {
              console.log(error);
            });
        })
        .catch((error) => {
          console.log(error);
        });
    },
    handleAreaDate(date){
      this.leafAreaValueData.leafAreaNDate = date;
    },
    handleCount(date){
      this.leafAreaValueData.leafAreaNDate = date;
    },
    close: function () {
      this.isDialog = false;
    },
    saveNew: function () {
      console.log("saveNew");
      const data = {
        deviceId: this.device.id,
        date: this.leafAreaValueData.leafAreaNDate,
        count: this.leafAreaValueData.leafAreaNValue,
      };
      // console.log("--- data ---");
      console.log(data);
      //新梢数の登録
      useLeafValueShoot(data)
        .then((response) => {
          const { status, message } = response["data"];
          if (status === 0) {
            alert("登録が完了しました。");
            this.isDialog = false;
          } else {
            alert("登録が失敗しました。");
            throw new Error(message);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    saveArea: function (id) {
      console.log("saveArea");
      // console.log(this.leafAreaValueData.leafAreaAreaDataList[id]);
      const data = {
        deviceId: this.device.id,
        date: this.leafAreaValueData.leafAreaAreaDataList[id].leafAreaADate,
        count: this.leafAreaValueData.leafAreaAreaDataList[id].leafAreaAValue,
        averageArea:
          this.leafAreaValueData.leafAreaAreaDataList[id].leafAreaAValueAverage,
        totalArea:
          this.leafAreaValueData.leafAreaAreaDataList[id].leafAreaAValueTotal,
      };
      // console.log("--- data ---");
      console.log(data);

      //新梢辺り葉枚数・平均個葉面積登録処理
      useLeafValueAreaAndCount(data)
        .then((response) => {
          const { status, message } = response["data"];
          if (status === 0) {
            alert("登録が完了しました。");
            this.isDialog = false;
          } else {
            alert("登録が失敗しました。");
            throw new Error(message);
          }
          //          this.shared.onConclude(this.value);
        })
        .catch((error) => {
          console.log(error);
        });
    },
    addrow: function () {
      this.leafAreaValueData.leafAreaAreaDataList.push({
        id: this.leafAreaValueData.leafAreaAreaDataList.length,
        leafAreaADate: moment().format("YYYY-MM-DD"),
        leafAreaAValue: 0,
        leafAreaAValueAverage: 0,
        leafAreaAValueTotal: 0,
      });
    },

    onChangeValue: function (id) {
      const aValue =
        this.leafAreaValueData.leafAreaAreaDataList[id].leafAreaAValue;
      const aValueAverage =
        this.leafAreaValueData.leafAreaAreaDataList[id].leafAreaAValueAverage;
      this.leafAreaValueData.leafAreaAreaDataList[id].leafAreaAValueTotal =
        aValue * aValueAverage;
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@/style/common.css";
// .GS_unit_text {
//   margin-top: 1.7rem;
//   font-style: italic;
//   color: gray;
//   font-size: 0.8em;
//   width: auto;
// }

// .container {
//   display: flex;
//   flex-flow: wrap;
//   align-items: center;
// }
// .fields {
//   display: flex;
//   padding: 3pt;
//   font-size: 11pt;
//   width: 90%;
//   flex-wrap: wrap;
// }
</style>
