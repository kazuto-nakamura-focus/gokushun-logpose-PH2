<template>
  <v-app>
    <v-container class="spacing-playground pa-5" fluid>
      <!-- ボタン選択エリア -->
      <targetMenu
        ref="targetMenu"
        :shared="sharedMenu"
        :model="isModel"
      ></targetMenu>
      <div v-show="CanShowSublist == true">
        <ButtonSelector
          :titleWidth="100"
          :title="menuTitle"
          :data="sensorList"
          :handleClick="handleClickSensor"
          :multiple="false"
        />
      </div>

      <v-expand-transition>
        <div v-if="bodyStatus == true">
          <template>
            <v-container>
              <v-row class="ma-1">
                <v-col cols="3">
                  <v-menu
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="auto"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                        dense
                        hide-details="auto"
                        outlined
                        v-bind="attrs"
                        v-on="on"
                        v-model="startDate"
                        append-icon="mdi-calendar-blank"
                      ></v-text-field>
                    </template>
                    <v-date-picker
                      v-model="startDate"
                      @input="menu1 = false"
                      @change="handleChangeDate()"
                    ></v-date-picker>
                  </v-menu>
                </v-col>
                <div class="mt-5">&nbsp;～&nbsp;</div>
                <v-col cols="3">
                  <v-menu
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="auto"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                        dense
                        hide-details="auto"
                        outlined
                        v-bind="attrs"
                        v-on="on"
                        v-model="endDate"
                        append-icon="mdi-calendar-blank"
                      ></v-text-field>
                    </template>
                    <v-date-picker
                      v-model="endDate"
                      @input="menu2 = false"
                      @change="handleChangeDate()"
                    ></v-date-picker>
                  </v-menu>
                </v-col>
                <v-col cols="3">
                  <v-row>
                    <v-col cols="4">インターバル</v-col>
                    <v-col>
                      <v-select
                        v-model="interval"
                        :items="intervalMenu"
                        item-text="name"
                        item-value="interval"
                        width="60"
                        dense
                        @change="handleChangeInterval"
                        return-object
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
              <v-row v-if="isInervalChanged">
                <v-col cols="12">
                  <b
                    >期間が５日以上の表示の場合は１時間単位の表示となります。</b
                  >
                </v-col>
              </v-row>
              <v-row v-if="is10MinutesEnabled">
                <v-col cols="12">
                  <b
                    >期間が５日以内の表示の場合は１０分単位の表示が可能です。</b
                  >
                </v-col>
              </v-row>
            </v-container>
          </template>
          <!-- 時間選択 -->
        </div>
      </v-expand-transition>
      <sensorGraphContainer ref="gfa"></sensorGraphContainer>
    </v-container>
  </v-app>
</template>

<script>
import moment from "moment";
import targetMenu from "@/components/parts/Ph2TargetMenu.vue";
import ButtonSelector from "@/components/parts/Ph2ButtonSelector.vue";
import sensorGraphContainer from "@/components-v1/parts/graph/生データグラフ/SensorGraphContainer.vue";
import { MountController } from "@/lib/mountController.js";
import { useSensoreList /*, useSensoreData*/ } from "@/api/SensorDataAPI.js";

export default {
  data() {
    return {
      isModel: false,
      sharedMenu: new MountController(),
      selectedItems: null,
      selectedSensor: null,

      // インターバルメニュー
      intervalMenu: [
        //     { interval: 10, name: "10分" },
        { interval: 60, name: "1時間" },
        { interval: 120, name: "2時間" },
      ],
      // インターバル
      interval: null,
      isInervalChanged: false,
      is10MinutesEnabled: false,

      menuTitle: "センサー",
      sensorList: [],
      CanShowSublist: false,

      bodyStatus: false,
      startDate: moment().add(-1, "months").format("YYYY-MM-DD"),
      endDate: moment().format("YYYY-MM-DD"),
    };
  },
  components: {
    targetMenu,
    ButtonSelector,
    sensorGraphContainer,
  },
  mounted() {
    this.interval = this.intervalMenu[0];
    this.sharedMenu.setUp(
      this.$refs.targetMenu,
      function (menu) {
        menu.initialize();
      },
      function (status, selected) {
        if (!status) {
          this.unset();
        } else {
          // 選択されたボタンの内容を取得
          this.selectedItems = selected;
          // センサーボタンの設定
          this.setSensorList();
        }
      }.bind(this)
    );
  },
  methods: {
    //* -------------------------------------------
    // センサーリストの取得
    //* -------------------------------------------
    setSensorList: function () {
      useSensoreList(this.selectedItems.selectedDevice.id)
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status === 0) {
            this.sensorList.length = 0;
            for (const item of data) {
              const data = {
                id: item.id,
                name: item.name,
                contentId: item.contentId,
                active: false,
              };
              this.sensorList.push(data);
            }
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //* -------------------------------------------
    // センサーリストが選択された時の処理
    //* -------------------------------------------
    handleClickSensor: function (item, index, active) {
      if (active == false) {
        this.sensorList.length = 0;
        this.bodyStatus = false;
        this.selectedSensor = null;
      } else {
        this.bodyStatus = true;
        this.selectedSensor = item;
        this.setGraph();
      }
    },
    //* -------------------------------------------
    // 日付変更時
    //* -------------------------------------------
    handleChangeDate: function () {
      // * インターバル変更表示のクリア
      this.isInervalChanged = false;
      this.is10MinutesEnabled = false;
      //*********************************
      //* インターバルの調整とリストの調整
      //*********************************
      // * 表示期間の取得
      const std = moment(this.startDate, "YYYY-MM-DD");
      const end = moment(this.endDate, "YYYY-MM-DD");
      const elapsedDate = end.diff(std, "days");
      // * 表示期間が５日以内の場合、メニューに１０分間隔を追加する
      if (elapsedDate <= 5) {
        this.intervalMenu = [
          { interval: 10, name: "10分" },
          { interval: 60, name: "1時間" },
          { interval: 120, name: "2時間" },
        ];
        this.is10MinutesEnabled = true;
      }
      // * 表示期間が５日以上の場合、メニューから１０分間隔を削除する
      else {
        this.intervalMenu = [
          { interval: 60, name: "1時間" },
          { interval: 120, name: "2時間" },
        ];
        //  インターバルが１０分間隔の場合は一時間に変更し、表示を変える
        if (this.interval.interval < 60) {
          this.interval = { interval: 60, name: "1時間" };
          this.isInervalChanged = true;
        }
      }
      this.setGraph();
    },
    //* -------------------------------------------
    // インターバル変更時
    //* -------------------------------------------
    handleChangeInterval: function () {
      this.setGraph();
    },

    //* -------------------------------------------
    // グラフデータの処理
    //* -------------------------------------------
    setGraph() {
      const dateText =
        this.selectedSensor.name + ":" + this.startDate + "～" + this.endDate;
      // * グラフタイトルの作成
      const title = {};
      title.main =
        this.selectedItems.selectedField.name +
        "|" +
        this.selectedItems.selectedDevice.name;
      title.sub = this.selectedSensor.name;
      /*  console.log(
        this.startDate,
        this.endDate,
        this.interval.interval,
        this.selectedSensor.name,
        dateText
      );*/
      this.$refs.gfa.setGraphData(
        title,
        this.selectedItems.selectedDevice.id,
        this.selectedSensor.contentId,
        this.selectedSensor.id,
        this.startDate,
        this.endDate,
        this.interval.interval,
        this.selectedSensor.name,
        dateText
      );
    },

    unset() {
      this.sensorList.length = 0;
      this.CanShowSublist = false;
    },
  },

  watch: {
    sensorList: {
      immediate: true,
      handler: function (newVal) {
        this.CanShowSublist = newVal.length > 0;
      },
    },
  },
};
</script>

<style lang="scss">
.view_data_table {
  border: solid 1px;
}
</style>
