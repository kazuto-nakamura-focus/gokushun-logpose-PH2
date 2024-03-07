<template>
  <div>
    <v-card elevation="6" v-if="titles.length > 0">
      <v-tabs grow bg-color="teal-accent-1" v-model="tab">
        <v-tab v-for="title in titles" :key="title.id">
          <div style="display: block">
            <div>
              {{ title.name }}
            </div>
            <div style="font-size: 9pt">
              {{ title.subname }}
            </div>
          </div>
        </v-tab>
        <v-tab-item v-for="item in graphList" :key="item.id">
          <ph-2-graph-component
            :key="item.id"
            :target="item"
            @delete="deleteItem"
          />
        </v-tab-item>
      </v-tabs>
    </v-card>
  </div>
</template>
<script>
import Ph2GraphComponent from "@/components/graph/Ph2GraphComponent.vue";

export default {
  //* ============================================
  // 使用コンポーネントリスト
  //* ============================================
  components: { Ph2GraphComponent },
  //* ============================================
  // データ
  //* ============================================
  data() {
    return {
      titles: [],
      id: 0,
      graphList: [], // グラフデータリスト
      item: {},
      tab: null,
    };
  },
  methods: {
    //* ============================================
    // グラフ追加処理
    //* ============================================
    addGraph: function (titleObj, chartOptions, chartData, isMultiple, name) {
      console.log("q");
      let title = {
        id: this.id,
        name: titleObj.main,
        subname: titleObj.sub,
      };
      this.titles.push(title);
      let item = {
        id: this.id++, // ID
        title: title.sub, // グラフタイトル
        options: chartOptions, // グラフオプション
        data: chartData, // グラフデータ
        isMultiple: isMultiple, // 単一グラフか複数グラフか
        name: name, // グラフ線の名前
      };
      this.graphList.push(item);
      this.tab = this.graphList.length - 1;
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
      this.titles.forEach((item, index) => {
        if (item.id == id) {
          this.titles.splice(index, 1);
        }
      });
      if (this.graphList.length > 0) {
        this.tab = this.graphList.length - 1;
      }
    },
  },
};
</script>