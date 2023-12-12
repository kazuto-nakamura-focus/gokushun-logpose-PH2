<template>
  <v-app>
    <v-container fluid>
      <ButtonSelector
        :title="grouthTitle"
        :data="fieldLIst"
        :handleClick="handleClickGrouth"
        :multiple="false"
      />
      <v-row><v-divider /></v-row>
      <v-expand-transition>
        <div v-if="selectedField != null">
          <ButtonSelector
            :title="deviceTitle"
            :data="deviceLIst"
            :handleClick="handleClickDevice"
            :multiple="false"
          />
          <v-row><v-divider /></v-row>
        </div>
      </v-expand-transition>

      <v-expand-transition>
        <div v-if="selectedDevice != null">
          <ButtonSelector
            :title="yearTitle"
            :data="yearList"
            :handleClick="handleClickYear"
            :multiple="false"
          />
          <v-row><v-divider /></v-row>
        </div>
      </v-expand-transition>

      <Ph2ModelControlArea
        ref="mdl"
        v-show="
          selectedField != null &&
          selectedDevice != null &&
          selectedYear != null
        "
      />

      <Ph2GraphArea
        ref="chr"
        v-show="
          selectedField != null &&
          selectedDevice != null &&
          selectedYear != null
        "
      />
    </v-container>
  </v-app>
</template>

<script>
import { GrowthChart } from "@/lib/graph/ApexCharts/GrowthChart.js";
import Ph2ModelControlArea from "@/components/parts/Ph2ModelControlArea.vue";
import Ph2GraphArea from "@/components/parts/Ph2GraphArea.vue";

import ButtonSelector from "@/components/parts/Ph2ButtonSelector";
import {
  useModelData,
  useLeafGraphAreaByParamSet,
  usePhotosynthesisGraphByParamSet,
} from "@/api/TopStateGrowth";

export default {
  data() {
    return {
      grouthTitle: "圃場",
      yearTitle: "年度",
      deviceTitle: "デバイス",

      fieldLIst: [],
      deviceLIst: [],
      yearList: [],
      targets: [],

      GrowthChart: new GrowthChart(),

      // 選択データリスト
      selectedField: null,
      selectedDevice: null,
      selectedYear: null,
      selectedItem: {},

      modelId: 0,
    };
  },
  components: {
    Ph2ModelControlArea,
    Ph2GraphArea,
    ButtonSelector,
  },
  methods: {
    //* --------------------------------------------
    //* 圃場・デバイス・年度のボタン作成
    //* --------------------------------------------
    createTargets(id, targets) {
      this.modelId = id;
      this.targets = targets;
      var map = new Map();
      for (const data of targets) {
        map.set(data.fieldId, data.field);
      }
      var entries = map.entries();
      for (var entry of entries) {
        var field = {
          name: entry[1],
          id: entry[0],
          state: false,
        };
        this.fieldLIst.push(field);
      }
    },
    //* --------------------------------------------
    //* グラフデータ生成
    //* --------------------------------------------
    setGraphData: function () {
      this.selectedItem = {
        modelId: this.modelId,
        field: this.selectedField.name,
        device: this.selectedDevice.name,
        year: this.selectedYear.id,
      };
      
      if (this.modelId == 1) {
        useModelData(this.selectedDevice.id, this.selectedYear.id)
          .then((response) => {
            // 成功時
            const results = response["data"].data;

            // グラフの表示オプションを設定
            let gc = new GrowthChart();
            gc.setOptions("生育ステージ推定モデル", "日付", "累積F値", results);
            // グラフ表示を行う
            this.$refs.chr.addGraph(
              this.selectedItem,
              gc.data.chartOptions,
              results
            );
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          });
      } else if (this.modelId == 2) {
        useLeafGraphAreaByParamSet(this.selectedDevice.id, this.selectedYear.id)
          .then((response) => {
            //成功時

            const results = response["data"].data[0];

            // グラフの表示オプションを設定
            let gc = new GrowthChart();
            gc.setOptions("葉面積推定モデル", "日付", "葉面積", results);
            // グラフ表示を行う
            this.$refs.chr.addGraph(
              this.selectedItem,
              gc.data.chartOptions,
              results
            );
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          });
      } else if (this.modelId == 3) {
        usePhotosynthesisGraphByParamSet(
          this.selectedDevice.id,
          this.selectedYear.id
        )
          .then((response) => {
            //成功時
            const results = response["data"].data;

            // グラフの表示オプションを設定
            let gc = new GrowthChart();
            gc.setOptions("光合成推定モデル", "日付", "光合成量", results);
            // グラフ表示を行う
            this.$refs.chr.addGraph(
              this.selectedItem,
              gc.data.chartOptions,
              results
            );
            console.log(results);
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          });
      }
    },
    //* --------------------------------------------
    //* 圃場を選択した時の処理
    //* --------------------------------------------
    handleClickGrouth: function (item, index, active) {
      //* 下位のボタンをクリアする
      this.deviceLIst.length = 0;
      this.selectedDevice = null;
      this.yearList.length = 0;
      this.selectedYear = null;
      active == true
        ? (this.selectedField = item)
        : (this.selectedField = null);
      if (active) {
        var map = new Map();
        for (const data of this.targets) {
          if (item.id == data.fieldId) {
            map.set(data.deviceId, data.device);
          }
        }
        var entries = map.entries();
        this.deviceLIst.length = 0;
        for (var entry of entries) {
          var field = {
            name: entry[1],
            id: entry[0],
            state: false,
          };
          this.deviceLIst.push(field);
        }
      }
    },
    //* --------------------------------------------
    //* デバイスを選択した時の処理
    //* --------------------------------------------
    handleClickDevice: function (item, index, active) {
      //* 下位のボタンをクリアする
      this.yearList.length = 0;
      this.selectedYear = null;
      active == true
        ? (this.selectedDevice = item)
        : (this.selectedDevice = null);
      if (active) {
        var map = new Map();
        for (const data of this.targets) {
          if (item.id == data.deviceId) {
            var field = {
              deviceId: data.deviceId,
              name: data.year,
              id: data.year,
              state: false,
            };
            map.set(data.yearId, field);
          }
        }
        var entries = map.entries();
        for (var entry of entries) {
          this.yearList.push(entry[1]);
        }
      }
    },
    //* --------------------------------------------
    //* 年度を選択した時の処理
    //* --------------------------------------------
    handleClickYear: function (item, index, active) {
      active == true ? (this.selectedYear = item) : (this.selectedYear = null);

      if (active) {
        // 選択をSTORE
        //* 圃場
        this.$store.dispatch("changeSelectedField", this.selectedField);
        //* デバイス名
        this.$store.dispatch("changeSelectedDevice", this.selectedDevice);
        //* 年度
        this.$store.dispatch("changeSelectedYear", this.selectedYear);
        //* コントロール表示
        this.$refs.mdl.setControll(this.modelId);
        //* グラフ表示
        this.setGraphData();
      }
    },
  },
};
</script>

<style lang="scss">
// TODO
// ヘッダー部
.fields {
  display: flex;
  padding: 3pt;
  font-size: 11pt;
  width: 90%;
  flex-wrap: wrap;
}

.menu {
  width: 90%;
  display: flex;
  justify-content: space-strech;
  flex-wrap: wrap;
}

.date {
  //display: flex;
  font-size: 11pt;
  // width: 80%;
  //justify-content: space-strech;
  //flex-wrap: wrap;
}
</style>
