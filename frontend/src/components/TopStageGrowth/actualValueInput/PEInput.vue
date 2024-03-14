<!--実績値入力-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="700" persistent>
        <v-card>
          <v-card-title v-if="title != null">実績値入力</v-card-title>
          <v-card-text>(PAMで計測した「f」と「g」の値を入力してください。)</v-card-text>
          <!-- タイトル部分 -->
          <input-header ref="titleHeader" />
          <!-- 入力部分 -->
          <v-container>
            <div class="text-subtitle-1">イールド値モデルパラメータ</div>
            <div>
              <v-container class="sprout">
                <v-row>
                  <v-col cols="1">
                    <v-row>
                      <v-subheader class="ma-0 pa-1">実績日</v-subheader>
                    </v-row>
                  </v-col>
                  <v-col cols="4">
                    <div style="margin-top: -10px; padding: 0">
                      <ph-2-date-picker
                        ref="date"
                        width="100%"
                        @onChange="handleDate"
                        style="margin: 0; padding: 0"
                        dense
                      />
                    </div>
                  </v-col>

                  <v-col cols="3">
                    <v-row>
                      <v-subheader class="ma-0 pa-1">f</v-subheader>
                      <v-text-field
                        class="ma-0 pa-1"
                        dense
                        hide-details="auto"
                        outlined
                        v-model.number="
                          photosynthesisValueData.photosynthesisValueF
                        "
                      ></v-text-field>
                    </v-row>
                  </v-col>
                  <v-col cols="3">
                    <v-row>
                      <v-subheader class="ma-0 pa-1">g</v-subheader>

                      <v-text-field
                        class="ma-0 pa-1"
                        dense
                        hide-details="auto"
                        outlined
                        v-model.number="
                          photosynthesisValueData.photosynthesisValueG
                        "
                      ></v-text-field>
                    </v-row>
                  </v-col>
                </v-row>
              </v-container>
            </div>
          </v-container>

          <div class="GS_ButtonArea">
            <v-btn color="primary" class="ma-2 white--text" elevation="2" @click="register()">保存</v-btn>
            <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="close()">閉じる</v-btn>
          </div>
        </v-card>
      </v-dialog>
    </v-container>
  </v-app>
</template>
<script>
import moment from "moment";
import { mdiExitToApp } from "@mdi/js";
import Ph2DatePicker from "@/components/parts/Ph2DatePicker.vue";
import InputHeader from "./InputHeader.vue";

import {
  usePhotosynthesisValuesUpdate,
  usePhotosynthesisValuesDetail,
} from "@/api/TopStateGrowth/PEActualValueInput";

export default {
  name: "PEActualValueInput",
  props: {
    shared /** MountController */: { required: true },
    selectedField: Array,
    selectedDevices: Array,
  },

  data() {
    return {
      selectedItems: {
        selectedField: this.$store.getters.selectedField,
        selectedDevice: this.$store.getters.selectedDevice,
        year: this.$store.getters.selectedYear,
      },
      value: "",
      date: moment().format("YYYY-MM-DD"),
      isDialog: false,
      title: "", // 選ばれたモデル種別
      field: {}, // 選ばれた圃場
      // selected: null,
      device: {},
      // params: [],
      path: mdiExitToApp,
      menu: false,
      year: 0,
      isUpdated: false,
      selectedDate: null,

      // headers: HEADERS,

      photosynthesisValueData: {
        photosynthesisDate: moment().format("YYYY-MM-DD"),
        photosynthesisValueF: 0,
        photosynthesisValueG: 0,
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
          this.$refs.date.initialize(data.menu.selectedYear);
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
      this.isUpdated = false;
      //this.isDialog = true;

      //光合成推定実績取得
      usePhotosynthesisValuesDetail(this.device.id, this.selectedDate)
        .then((response) => {
          console.log(response);
          const ps_data = response["data"]["data"];
          this.photosynthesisValueData.photosynthesisValueF = ps_data.f;
          this.photosynthesisValueData.photosynthesisValueG = ps_data.g;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    handleDate(date) {
      this.date = date;
    },
    close: function () {
      this.isDialog = false;
      this.shared.onConclude(this.isUpdated);
    },
    register: function () {
      const data = {
        deviceId: this.device.id,
        date: this.selectedDate,
        f: this.photosynthesisValueData.photosynthesisValueF,
        g: this.photosynthesisValueData.photosynthesisValueG,
      };

      // console.log("--- data ---");
      console.log(data);

      //光合成推定実績値更新
      usePhotosynthesisValuesUpdate(data)
        .then((response) => {
          console.log(response);
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
    handleChangeDate() {},
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
