<template>
  <span>
    <button class="action-button" data-action="inputDate" elevation-6>
      実績値入力
    </button>
  </span>
</template>
<script>
export default {
  name: "CellRender",
};
</script>

<style lang="scss" scoped>
.action-button {
  height: 30px;
  width: 96px;
  line-height: 20px;
  border: none;
  color: #ffffff;
  padding: 0px 0px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 12px;
  opacity: 1;
  background-color: rgb(0, 180, 40);
  border-radius: 5px;
  margin: 0px 4px;
}
</style>