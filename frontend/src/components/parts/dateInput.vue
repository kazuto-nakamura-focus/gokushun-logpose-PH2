<template>
  <div class="fields">
    <div style="margin: 5px 10px 5px 0">
      <date-picker
        style="width:200px"
        v-model="dates"
        @change="callGetTaskListByMember()"
        range
        format="YYYY-MM-DD"
        value-type="YYYY-MM-DD"
      ></date-picker>
    </div>
    <div style="padding: 5px">
      <v-btn
        depressed
        color="primary"
        elevation="3"
        @click="setData()"
        :disabled="isDisabled"
        >積算</v-btn
      >
    </div>
  </div>
</template>

<script>
import moment from "moment";
import DatePicker from "vue2-datepicker";
import "vue2-datepicker/index.css";
import "vue2-datepicker/locale/ja";

export default {
  data() {
    return {
      dates: [],
      isDisabled:true
    };
  },
 
  components: {
    DatePicker,
  },
  created: function () {
    const today = moment().format("YYYY-MM-DD");
    this.dates.push(today);
    this.dates.push(today);
  },
  methods: {
    setData: function () {
      this.$emit("onClick", this.dates);
    },
  }
};
</script>
<style lang="scss" scoped>
// ヘッダー部
.fields {
  display: flex;
  padding: 3pt;
  font-size: 11pt;
  width: auto;
  flex-wrap: wrap;
}
</style>
