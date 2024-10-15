<template>
  <div>
    <v-card elevation="6" v-if="graphList.length > 0">
      <v-tabs grow bg-color="teal-accent-1" v-model="tab">
        <v-tab v-for="item in graphList" :key="item.id">
          <div style="display: block">
            <div>{{ item.title }}</div>
            <div style="font-size: 9pt">{{ item.subname }}</div>
          </div>
        </v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item v-for="item in graphList" :key="item.id">
          <ph-2-model-graph
            v-if="graphType == '1'"
            :key="item.id"
            :target="item"
            @delete="deleteItem"
            @doGraphAction="doGraphAction"
            :ref="'graph-' + item.id"
          />
          <ph-2-sensor-graph
            v-if="graphType == '2'"
            :key="item.id"
            :target="item"
            @delete="deleteItem"
            :ref="'graph-' + item.id"
          />
        </v-tab-item>
      </v-tabs-items>
    </v-card>
  </div>
</template>
<script>
import Ph2ModelGraph from "@/components-v1/parts/graph/モデルグラフ/Ph2ModelGraph.vue";
import Ph2SensorGraph from "@/components-v1/parts/graph/生データグラフ/Ph2SensorGraph.vue";

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
  watch: {
    //* ============================================
    // タブが切り替わった時に対応する関数を呼び出す
    //* ============================================
    tab(newTab) {
      this.handleTabChange(newTab);
    },
  },
  methods: {
    //* ============================================
    // グラフ追加処理
    //* ============================================
    addGraph: function (graphPanel) {
      graphPanel.id = this.id;
      this.graphList.push(graphPanel);
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
    //* ============================================
    // タブが切り替わった時にX軸のラベルが消える問題の対応
    //  再表示を強制する
    //* ============================================
    handleTabChange(tabIndex) {
      const graphRef = this.$refs[`graph-${tabIndex}`];
      if (graphRef !== undefined) graphRef[0].setXLabel();
    },
  },
};
</script>