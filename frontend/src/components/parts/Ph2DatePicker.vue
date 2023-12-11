<!--実績値入力-->
<template>
  <div>
    <v-menu
      v-model="menu"
      :close-on-content-click="false"
      :nudge-right="10"
      transition="scale-transition"
      offset-y
      filled
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
          clearable
          dense
        ></v-text-field>
      </template>

      <v-date-picker
        v-model="date"
        :min="minDate"
        :max="maxDate"
        @input="menu = false"
        @change="handleChangeDate"
        locale="jp-ja"
      ></v-date-picker>
    </v-menu>
  </div>
</template>
  <script>
import moment from "moment";

export default {
  data() {
    return {
      date: moment().format("YYYY-MM-DD"),
      minDate: null,
      maxDate: null,
      menu: false,
      id: null,
    };
  },

  methods: {
    initialize: function (selectedYear, id) {
      // 年度
      const year = selectedYear.id;
      // 開始日
      this.minDate = year.toString() + "-" + selectedYear.startDate;
      // 開始日からmoment
      const momentData = moment(this.minDate);
      this.minDate = momentData.format("YYYY-MM-DD");
      // 最終日
      this.maxDate = moment().format("YYYY-MM-DD");
      // このコンポーネントのID
      this.id = id;
      this.$emit("onChange", this.date, this.id);
    },
    initializeByDate: function (selectedYear, id, date) {
      // 年度
      const year = selectedYear.id;
      // 開始日
      this.minDate = year.toString() + "-" + selectedYear.startDate;
      // 開始日からmoment
      const momentData = moment(this.minDate);
      this.minDate = momentData.format("YYYY-MM-DD");
      // 最終日
      this.maxDate = moment().format("YYYY-MM-DD");
      // このコンポーネントのID
      this.id = id;
      this.date = date;
    },
    setDate(date) {
      this.date = date;
    },
    handleChangeDate() {
      if (this.date < this.minDate || this.date > this.maxDate) {
        alert("日付は年度内を指定してください。");
      } else {
        this.$emit("onChange", this.date, this.id);
      }
    },
  },
};
</script>F
  <style lang="scss" scoped>
@import "@/style/common.css";
</style>
  