<template>
  <v-app>
    <v-container  fluid>
      <ButtonSelector
        :title="modelTitle"
        :data="menuModel"
        :handleClick="handleClickModel"
        :multiple="false"
      />
      <v-divider />
    
    </v-container>
  </v-app>
</template>

<script>
//mport ph2ModelContainer from "./Ph2ModelContainer.vue";
import ButtonSelector from "@/components/parts/Ph2ButtonSelector";
import { useModels } from "@/api/Top";

export default {
  data() {
    return {
      menuModel: [],
      targets: [],
      modelTitle: "モデル",
      selectedModel: 0,
      refs:[],
    };
  },

  mounted() {
    useModels()
      .then((response) => {
        //成功時
        const results = response["data"].data;
        var models = results.models;
        //* モデルコンポーネントの作成
        for (const item of models) {
          //* モデルボタンの作成
          var label = {
            name: item.name,
            id: item.id,
            state: false,
          };
          this.menuModel.push(label);
        }
        this.targets = results.targets;
      })
      .catch((error) => {
        //失敗時
        console.log(error);
      });
  },
  components: {
  //  ph2ModelContainer,
    ButtonSelector,
  },
  methods: {
    handleClickModel: function (item, index, active) { 
      if (active == true) {
        this.selectedModel = item.id;
        document.getElementById("m"+item.id).__vue__.createTargets(item.id, this.targets);
      }
    },
  },
};
</script>

<style lang="scss">
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
