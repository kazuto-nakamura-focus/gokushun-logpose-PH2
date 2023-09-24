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
                      >
                      </v-text-field>
                    </template>
                    <v-date-picker
                      v-model="startDate"
                      @input="menu1 = false"
                      @change="handleChangeDate()"
                    >
                    </v-date-picker>
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
                      >
                      </v-text-field>
                    </template>
                    <v-date-picker
                      v-model="endDate"
                      @input="menu2 = false"
                      @change="handleChangeDate()"
                    >
                    </v-date-picker>
                  </v-menu>
                </v-col>
                <v-col cols="1">
                  <div>
                    <v-select
                      v-model="selectedHour"
                      :items="hoursMenu"
                      item-text="name"
                      item-value="hour"
                      width="60"
                      dense
                      @change="handleChangeDate()"
                      return-object
                    ></v-select>
                  </div>
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
import sensorGraphContainer from "@/components/graph/SensorGraphContainer.vue";
import { MountController } from "@/lib/mountController.js";
import { useSensoreList /*, useSensoreData*/ } from "@/api/SensorDataAPI.js";

//* --------------------------------------------------
// 時間メニューの設定
//* --------------------------------------------------
var setHoursMenu = function(hoursMenu) {
  for(let i=1;i<13;i++){
    const item = {
      hour:i,
      name : i + "時"
    }
    hoursMenu.push(item);
  }
}

export default {
  data() {
    return {
      isModel: false,
      sharedMenu: new MountController(),
      selectedItems: null,
      selectedSensor: null,
      selectedHour: null,

      hoursMenu: [],
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
    //* 時間メニューの設定
    setHoursMenu(this.hoursMenu);
    this.selectedHour = this.hoursMenu[11];

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
      this.setGraph();
    },
    //* -------------------------------------------
    // 時間変更時
    //* -------------------------------------------
    changeHour: function () {
      this.setGraph();
    },

    //* -------------------------------------------
    // グラフデータの処理
    //* -------------------------------------------
    setGraph() {
      const dateText = "(" + this.startDate + "～" + this.endDate + ")";
      const title =
        this.selectedItems.selectedField.name +
        ">" +
        this.selectedItems.selectedDevice.name +
        ">" +
        this.selectedSensor.name +
        dateText;
      const std = moment(this.startDate, "YYYY-MM-DD");
      const end = moment(this.endDate, "YYYY-MM-DD");
      const elapsedDate = end.diff(std, "days");
      const type = elapsedDate > 6 ? 0 : 1;
      this.$refs.gfa.setGraphData(
        title,
        this.selectedSensor.contentId,
        this.selectedSensor.id,
        this.startDate,
        this.endDate,
        type,
        this.selectedHour.hour
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
