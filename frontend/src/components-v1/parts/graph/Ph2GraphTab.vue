<template>
  <div>
    <v-card elevation="6" v-if="graphList.length > 0">
      <v-tabs grow bg-color="teal-accent-1" v-model="tab">
        <v-tab v-for="item in graphList" :key="item.id">
          <div style="display: block">
            <div>{{ item.title.name }}</div>
            <div style="font-size: 9pt">{{ item.title.subname }}</div>
          </div>
        </v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item v-for="item in graphList" :key="item.id">
          <ph-2-model-graph
            v-if="graphType=='1'"
            :key="item.id"
            :target="item.graph"
            @delete="deleteItem"
            @doGraphAction="doGraphAction"
          />
          <ph-2-sensor-graph
            v-if="graphType=='2'"
            :key="item.id"
            :target="item.graph"
            @delete="deleteItem"
          />
        </v-tab-item>
      </v-tabs-items>
    </v-card>
  </div>
</template>
<script>
import Ph2ModelGraph from "@/components-v1/parts/graph/Ph2ModelGraph.vue";
import Ph2SensorGraph from "@/components-v1/parts/graph/Ph2SensorGraph.vue";

export default {
  props: {
    graphType: String,
  },
  //* ============================================
  // 使用コンポーネントリスト
  //* ============================================
  components: { Ph2ModelGraph, Ph2SensorGraph },
  //* ============================================
  // データ
  //* ============================================
  data() {
    return {
      graphList: [],
      id: 0,
      tab: null,
    };
  },
  methods: {
    //* ============================================
    // グラフ追加処理
    //* ============================================
    addGraph: function (
      titleObj,
      chartOptions,
      chartData,
      isMultiple,
      name,
      selectedItems
    ) {
      let title = {
        id: this.id,
        name: titleObj.main,
        subname: titleObj.sub,
      };
      let graph = {
        id: this.id, // ID
        title: title.sub, // グラフタイトル
        options: chartOptions, // グラフオプション
        data: chartData, // グラフデータ
        isMultiple: isMultiple, // 単一グラフか複数グラフか
        name: name, // グラフ線の名前
        selectedItems: selectedItems,
      };
      let tabContent = {
        id: this.id,
        title: title,
        graph: graph,
      };

      this.graphList.push(tabContent);
      this.tab = this.graphList.length - 1;
      let id = this.id++;
      return id;
    },
    //* ============================================
    // グラフアクション実行時
    //* ============================================
    doGraphAction: function (selectedItems) {
      this.$emit("doAction", selectedItems);
    },
    //* ============================================
    // グラフアイテムの削除
    //* ============================================
    deleteItem: function (id) {
      this.graphList.forEach((item, index) => {
        if (item.id == id) {
          this.graphList.splice(index, 1);
        }
      });
      if (this.graphList.length > 0) {
        this.tab = this.graphList.length - 1;
      }
    },
  },
};
</script>