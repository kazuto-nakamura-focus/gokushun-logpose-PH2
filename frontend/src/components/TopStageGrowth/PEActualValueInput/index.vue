<!--実績値入力-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="700">
        <v-card>
          <v-card-title v-if="title != null">実績値入力</v-card-title>
          <v-card-text>
            (PAMで計測した「f」と「g」の値を入力してください。)
          </v-card-text>
          <v-container>
            <v-row :align="align" no-gutters>
              <v-col :cols="4">
                <v-card-text>
                  圃場名<br />
                  <p class="font-weight-bold">{{ field.name }}</p>
                </v-card-text>
              </v-col>
              <v-col>
                <v-card-text>
                  デバイス名<br />
                  <p class="font-weight-bold">{{ device.name }}</p>
                </v-card-text>
              </v-col>
              <v-col>
                <v-card-text>
                  年度<br />
                  <p class="font-weight-bold">{{ year }}</p>
                </v-card-text>
              </v-col>
            </v-row>
            <div class="text-subtitle-1">
              イールド値モデルパラメータ
            </div>
            <div>
              <v-container class="sprout">
                <v-row>
                  <v-col cols="4">
                      <v-row>
                        <v-subheader class="ma-0 pa-1">実績日</v-subheader>
                        <v-menu
                          v-model="menu"
                          :close-on-content-click="false"
                          :nudge-right="40"
                          transition="scale-transition"
                          offset-y
                          min-width="auto"
                        >
                          <template v-slot:activator="{ on, attrs }">
                            <v-text-field
                              class="ma-0 pa-1"
                              v-model="photosynthesisValueData.photosynthesisDate"
                              v-bind="attrs"
                              v-on="on"
                              outlined
                              dense
                            ></v-text-field>
                          </template>
                          <v-date-picker
                            v-model="photosynthesisValueData.photosynthesisDate"
                            @input="menu = false"
                          ></v-date-picker>
                        </v-menu>
                      </v-row>
                  </v-col>
                  <v-col cols="6">
                      <v-row>
                          <v-subheader class="ma-0 pa-1">f</v-subheader>
                          <v-text-field class="ma-0 pa-1" dense hide-details="auto" outlined
                              v-model.number="photosynthesisValueData.photosynthesisValueF"></v-text-field>
                          <v-subheader class="ma-0 pa-1">g</v-subheader>
                          <v-text-field class="ma-0 pa-1" dense hide-details="auto" outlined
                              v-model.number="photosynthesisValueData.photosynthesisValueG"></v-text-field>
                      </v-row>
                  </v-col>
                </v-row>
              </v-container>
            </div>

          </v-container>

          <div class="GS_ButtonArea">
            <v-btn color="primary" class="ma-2 white--text" elevation="2" @click="save()">保存</v-btn>
            <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="close()">キャンセル</v-btn>
          </div>
        </v-card>
      </v-dialog>
    </v-container>
  </v-app>
</template>
<script>
import moment from "moment";
import { mdiExitToApp } from "@mdi/js";

import { 
  usePhotosynthesisValuesUpdate,
  usePhotosynthesisValuesDetail 
} from "@/api/TopStateGrowth/PEActualValueInput";

export default {
  name: 'PEActualValueInput',
  props: { 
    shared /** MountController */: { required: true } ,
    selectedField: Array,
    selectedDevices: Array
  },

  data() {
    return {
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
      year:0,

      // headers: HEADERS,

      photosynthesisValueData: {
        photosynthesisDate: moment().format("YYYY-MM-DD"),
        photosynthesisValueF: 0,
        photosynthesisValueG: 0,
      },
    };
  },

  components: {
    // SvgIcon,
  },

  mounted() {
    this.shared.mount(this);
  },
  methods: {
    initialize: function (data) {
      //年度
      this.year = this.$store.getters.selectedYear.id;
      // タイトル
      this.title = data.title;
      // 圃場
      this.field = {
        id: this.$store.getters.selectedField.id,
        name: this.$store.getters.selectedField.name,
      }
      // デバイス
      this.device = {
        id: this.$store.getters.selectedDevice.id,
        name: this.$store.getters.selectedDevice.name,
      }
      //this.isDialog = true;

      //光合成推定実績取得
      usePhotosynthesisValuesDetail(this.device.id, this.photosynthesisValueData.photosynthesisDate)
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
    close: function () {
      this.isDialog = false;
    },
    save: function () {
      const data = {
        deviceId: this.device.id,
        date: this.photosynthesisValueData.photosynthesisDate,
        f: this.photosynthesisValueData.photosynthesisValueF,
        g: this.photosynthesisValueData.photosynthesisValueG,
      }

      // console.log("--- data ---");
      console.log(data);

      //光合成推定実績値更新
      usePhotosynthesisValuesUpdate(data)
      .then((response) => {
        console.log(response);
        this.isDialog = false;
        this.shared.onConclude(this.value);
      })
      .catch((error) => {
        console.log(error);
      });
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
