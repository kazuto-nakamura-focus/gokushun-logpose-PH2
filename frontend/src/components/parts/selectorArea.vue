<template>
  <div>
    <div v-for="item in sourceData" :key="item.title">
      <span
        id="multi"
        v-bind:class="
          [item.state ? 'buttonActive ' + classPattern : 'buttonBase ' + classPattern]
        "
        v-on:click="changeState(item)"
        >{{ item.title }}</span
      >
    </div>
  </div>
</template>

<script>
export default {
  props: { shared /** MountController */: { required: true } },

  data() {
    return {
      // 複数選択可能かどうか
      isMultiSelection: false,
      // 表示するソースデータ
      sourceData: [],
      // CSSパターン
      classPattern : ""
    };
  },

  mounted() {
    this.shared.mount(this);
  },

  methods: {
    initialize: function (sourceData, isMultiSelection) {
      this.classPattern = (isMultiSelection) ? 'multiSelect' : 'singleSelect';
      this.sourceData = sourceData;
      this.isMultiSelection = isMultiSelection;
    },

    changeState: function (item) {
      item.state = !item.state;
      // 単一選択で選択がされた場合、他の選択済みは未選択に戻す
      if (!this.isMultiSelection && item.state) {
        for (const sourceItem of this.sourceData) {
          if (sourceItem === item) continue;
          sourceItem.state = false;
        }
      }
      this.shared.onConclude(this.sourceData);
    },
  },
};
</script>

<style lang="scss" scoped>
.singleSelect {
  border: 1px solid rgb(7, 151, 74);
  box-shadow: lightgray 1px 1px 1px 1px;
}
.multiSelect {
  border: 3px double rgb(7, 151, 74);
  box-shadow: lightgray 1px 1px 1px 1px;
}
.buttonBase {
  border-radius: 10px;
  width: auto;
  min-width: 100px;
  display: block;
  padding: 10px;
  box-sizing: border-box;
  color: rgb(7, 151, 74);
  text-decoration: none;
  text-align: center;
  margin: 10px 5px;
}
.buttonBase:hover {
  cursor: pointer;
  opacity: 1;
  color: white;
  background-color: rgb(7, 151, 74);
}
.push {
  box-shadow: rgba(9, 59, 2, 0.884) 0px 0px 6px 3px;
  -webkit-box-shadow: rgba(14, 81, 3, 0.059) 0px 0px 6px 3px;
  -moz-box-shadow: rgba(14, 81, 3, 0.059) 0px 0px 6px 3px;
}
.buttonBase:active {
  position: relative;
  top: 2px;
  box-shadow: none;
  color: rgb(7, 151, 74);
  background-color: white;
  margin: 10px 5px;
}
.buttonActive {
  border-radius: 10px;
  width: auto;
  min-width: 100px;
  border: 4px doublergb(7, 151, 74);
  display: block;
  padding: 10px;
  background-color: rgb(7, 151, 74);
  box-sizing: border-box;
  color: white;
  text-decoration: none;
  text-align: center;
  margin: 10px 5px;
}
.buttonActive:hover {
  border: 1px solid rgb(7, 151, 74);
  cursor: pointer;
  opacity: 1;
  color: white;
  background-color: rgb(7, 151, 74);
}
.buttonActive:active {
  position: relative;
  top: 2px;
  box-shadow: none;
  background-color: rgb(7, 151, 74);
}
</style>