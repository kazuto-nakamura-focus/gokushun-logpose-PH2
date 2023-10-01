<!--実績値入力-->
<template>
    <v-menu
      v-model="menu"
      :close-on-content-click="false"
      :nudge-right="10"
      transition="scale-transition"
      offset-y
      filled
      width="200"
      min-width="auto"
      dense
      style="margin: 0; padding: 0"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-text-field
          class="ma-0 pa-1"
          v-model="date"
          v-bind="attrs"
          v-on="on"
          outlined
          filled
          dense
        ></v-text-field>
      </template>

      <v-date-picker
        v-model="date"
        :min="minDate"
        :max="maxDate"
        @input="menu = false"
        @change="handleChangeDate"
      ></v-date-picker>
    </v-menu>
</template>
  <script>
import moment from "moment";

export default {
  props: { shared /** MountController */: { required: true } },
  data() {
    return {
      date: moment().format("YYYY-MM-DD"),
      minDate:null,
      maxDate: null,
      menu: true,
    };
  },

  methods: {
    initialize: function (selectedYear) {
      // 年度
      const year = selectedYear.id;
      // 開始日
      this.minDate = year.toString() + "-" + selectedYear.startDate;
      // 開始日からmoment
      const momentData = moment(this.minDate);
      this.minDate = momentData.format("YYYY-MM-DD");
      // 最終日
      this.maxDate = moment().format("YYYY-MM-DD");
    },

    handleChangeDate() {
      if (this.date < this.minDate || this.date > this.maxDate) {
        alert("日付は年度内を指定してください。");
      } else {
        this.$emit("onChange", this.date);
      }
    },
  },
};
</script>F
  <style lang="scss" scoped>
@import "@/style/common.css";
</style>
  