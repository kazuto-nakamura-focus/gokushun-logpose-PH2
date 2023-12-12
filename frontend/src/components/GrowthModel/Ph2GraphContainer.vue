<template>
  <v-app>
    <v-container>
      <Ph2GraphArea ref="chr" />
    </v-container>
  </v-app>
</template>
  
  <script>
import { GrowthChart } from "@/lib/graph/ApexCharts/GrowthChart.js";
import Ph2GraphArea from "../graph/Ph2GraphArea.vue";

import {
  useModelData,
  useLeafGraphAreaByParamSet,
  usePhotosynthesisGraphByParamSet,
} from "@/api/TopStateGrowth";

export default {
  data() {
    return {
      selectedItem: {},
      modelId: 0,
      //* ============================================
      //* 選択されたターゲットとグラフの関係
      //* ============================================
      DISPLAYED: {
        map: new Map(), // 表示済みMAP
        //* --------------------------------------------
        //* 作成されたグラフの登録
        //* --------------------------------------------
        add(selectedItems) {
          let key =
            selectedItems.selectedModel.id +
            "+" +
            selectedItems.selectedField.id +
            "+" +
            selectedItems.selectedDevice.id +
            "+" +
            selectedItems.selectedYear.id;
            this.map.set(key,
            {
              items : selectedItems,
              node : null
            });
        },
      },
    };
  },
  components: {
    Ph2GraphArea,
  },
  methods: {
    //* --------------------------------------------
    //* グラフデータ生成
    //* --------------------------------------------
    setGraphData: function (selectedItems) {
      this.$nextTick(
        function () {
          this.setData(selectedItems);
        }.bind(this)
      );
    },
    setData(selectedItems) {
      this.selectedItem = {
        modelId: selectedItems.selectedModel.id,
        field: selectedItems.selectedField.name,
        device: selectedItems.selectedDevice.name,
        year: selectedItems.selectedYear.id,
      };
      const titlePaths = this.selectedItem.field +">" + this.selectedItem.device +">" + this.selectedItem.year;
      this.modelId = this.selectedItem.modelId;
      if (this.modelId == 1) {
        useModelData(
          selectedItems.selectedDevice.id,
          selectedItems.selectedYear.id
        )
          .then((response) => {
            // 成功時
            const results = response["data"].data;

            // グラフの表示オプションを設定
            let gc = new GrowthChart();
            gc.setOptions("生育ステージ推定モデル", "日付", "累積F値", results);
            // グラフ表示を行う
            gc.setLoadingParent(this);
            this.$refs.chr.addGraph(
              titlePaths,
              gc.data.chartOptions,
              results,
              true,
              null
            );
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          });
      } else if (this.modelId == 2) {
        useLeafGraphAreaByParamSet(
          selectedItems.selectedDevice.id,
          selectedItems.selectedYear.id
        )
          .then((response) => {
            const results = response["data"];
            const status = results["status"];
            const responseData = results["data"];

            //生育ステージ以外の生育モデルは、Annotations削除
            if(status === 0){
              responseData.forEach((element) => {
                element["annotations"] = null;
              });
            }
            //成功時
            // グラフの表示オプションを設定
            let gc = new GrowthChart();
            gc.setLoadingParent(this);
            gc.setOptions(
              "葉面積推定モデル",
              "日付", 
              "葉面積(㎡)",
              response["data"].data[0]
            );
            console.log(response["data"].data[1]);
            // グラフ表示を行う
            this.$refs.chr.addGraph(
              titlePaths,
              gc.data.chartOptions,
              response["data"].data[0],
              true,
              null
            );
            // グラフの表示オプションを設定
            gc = new GrowthChart();
            gc.setLoadingParent(this);
            gc.setOptions(
              "葉枚数推定モデル",
              "日付", 
              "葉枚数(枚)",
              response["data"].data[1]
            );
            // グラフ表示を行う
            this.$refs.chr.addGraph(
              titlePaths,
              gc.data.chartOptions,
              response["data"].data[1],
              true,
              null
            );
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          });
      } else if (this.modelId == 3) {
        usePhotosynthesisGraphByParamSet(
          selectedItems.selectedDevice.id,
          selectedItems.selectedYear.id
        )
          .then((response) => {
            //成功時
            const responseData = response["data"];
            const status = responseData["status"];
            const results = responseData["data"];

            if(status === 0){
              results["annotations"] = null;
            }

            // グラフの表示オプションを設定
            let gc = new GrowthChart();
            gc.setLoadingParent(this);
            gc.setOptions("光合成推定モデル", "日付", "光合成量(kgCO2 vine^-1)", results);
            // グラフ表示を行う
            this.$refs.chr.addGraph(
              titlePaths,
              gc.data.chartOptions,
              results,
              true,
              null
            );
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          });
      }
    },
  },
};
</script>
  
