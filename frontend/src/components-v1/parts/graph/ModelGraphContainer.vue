<template>
  <v-app>
    <v-container>
      <Ph2GraphTabs graphType="1" ref="chr" @doAction="setData" />

      <wait-dialog ref="wait" />
    </v-container>
  </v-app>
</template>
  
<script>
import { GrowthChart } from "@/lib/graph/ApexCharts/GrowthChart.js";
import Ph2GraphTabs from "@/components-v1/parts/graph/Ph2GraphTab.vue";

import {
  useModelData,
  useLeafGraphAreaByParamSet,
  usePhotosynthesisGraphByParamSet,
} from "@/api/TopStateGrowth";
import WaitDialog from "@/components-v1/parts/dialog/WaitDialog.vue";

export default {
  data() {
    return {
      modelId: 0,
    };
  },
  components: {
    Ph2GraphTabs,
    WaitDialog,
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
      // * グラフがすでに描画済みであるならば、削除する
      if (selectedItems.selectedYear.graphId === undefined) {
        selectedItems.selectedYear.graphId = [];
      } else {
        for (const id of selectedItems.selectedYear.graphId) {
          this.$refs.chr.deleteItem(id);
        }
      }

      // * グラフタイトルの作成
      const title = {};
      title.main =
        selectedItems.selectedField.name +
        "|" +
        selectedItems.selectedField.name +
        "|" +
        selectedItems.selectedYear.id;
      title.sub = selectedItems.selectedModel.name;

      this.modelId = selectedItems.selectedModel.id;
      const year = selectedItems.selectedYear.id;
      if (this.modelId == 1) {
        this.$refs.wait.start("描画中です。しばらくお待ちください。", false);
        useModelData(
          selectedItems.selectedDevice.id,
          selectedItems.selectedYear.id
        )
          .then((response) => {
            // 成功時
            const results = response["data"].data;
            if (results == null) {
              alert("データがありません。");
              return;
            }
            // グラフの表示オプションを設定
            let gc = new GrowthChart();
            gc.setOptions(
              "生育ステージ推定モデル",
              "",
              "累積F値",
              results,
              year
            );
            // グラフ表示を行う
            gc.setLoadingParent(this);
            let graphId = this.$refs.chr.addGraph(
              title,
              gc.data.chartOptions,
              results,
              true,
              null,
              selectedItems
            );
            // グラフのIDリストに追加し、同じグラフが追加される場合は削除できるようにする。
            selectedItems.selectedYear.graphId.push(graphId);
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          })
          .finally(() => {
            this.$refs.wait.finish();
          });
      } else if (this.modelId == 2) {
        this.$refs.wait.start("描画中です。しばらくお待ちください。", false);
        useLeafGraphAreaByParamSet(
          selectedItems.selectedDevice.id,
          selectedItems.selectedYear.id
        )
          .then((response) => {
            const results = response["data"];
            const status = results["status"];
            const responseData = results["data"];
            if (results.data == null) {
              alert("データがありません。");
              return;
            }

            //生育ステージ以外の生育モデルは、Annotations削除
            if (status === 0) {
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
              "",
              "葉面積(㎡)",
              response["data"].data[0],
              year,
              2
            );
            console.log(response["data"].data[1]);
            // グラフ表示を行う
            let graphId = this.$refs.chr.addGraph(
              title,
              gc.data.chartOptions,
              response["data"].data[0],
              true,
              null,
              selectedItems
            );
            // グラフのIDリストに追加し、同じグラフが追加される場合は削除できるようにする。
            selectedItems.selectedYear.graphId.push(graphId);
            // グラフの表示オプションを設定
            gc = new GrowthChart();
            gc.setLoadingParent(this);
            gc.setOptions(
              "葉枚数推定モデル",
              "",
              "葉枚数(枚)",
              response["data"].data[1],
              year
            );
            // グラフ表示を行う
            graphId = this.$refs.chr.addGraph(
              title,
              gc.data.chartOptions,
              response["data"].data[1],
              true,
              null,
              selectedItems
            );
            // グラフのIDリストに追加し、同じグラフが追加される場合は削除できるようにする。
            selectedItems.selectedYear.graphId.push(graphId);
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          })
          .finally(() => {
            this.$refs.wait.finish();
          });
      } else if (this.modelId == 3) {
        this.$refs.wait.start("描画中です。しばらくお待ちください。", false);
        usePhotosynthesisGraphByParamSet(
          selectedItems.selectedDevice.id,
          selectedItems.selectedYear.id
        )
          .then((response) => {
            //成功時
            const responseData = response["data"];
            const status = responseData["status"];
            const results = responseData["data"];
            if (results == null) {
              alert("データがありません。");
              return;
            }
            if (status === 0) {
              results["annotations"] = null;
            }

            // グラフの表示オプションを設定
            let gc = new GrowthChart();
            gc.setLoadingParent(this);
            gc.setOptions(
              "光合成推定モデル",
              "",
              "光合成量(kgCO2 vine^-1)",
              results,
              year,
              5
            );
            // グラフ表示を行う
            let graphId = this.$refs.chr.addGraph(
              title,
              gc.data.chartOptions,
              results,
              true,
              null,
              selectedItems
            );
            // グラフのIDリストに追加し、同じグラフが追加される場合は削除できるようにする。
            selectedItems.selectedYear.graphId.push(graphId);
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          })
          .finally(() => {
            this.$refs.wait.finish();
          });
      }
    },
  },
};
</script>
  
