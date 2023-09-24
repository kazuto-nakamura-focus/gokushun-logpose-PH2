<template>
  <div id="app">
    <v-container>
      <VuePerfectScrollbar class="scroll-area" v-once :settings="settings">
        <div v-for="item in histories" :key="item.id" class="GS_history_row">
          <div class="GS_history_header">
            <div>
              {{ item.dateTime }}
            </div>
            <div style="min-width: 10px"></div>
            <div>
              {{ item.name }}
            </div>
          </div>
          <div class="GS_history_comment">
            {{ item.comment }}
          </div>
          <div class="GS_history_expression">
            {{ item.expression }}
          </div>
          <v-divider></v-divider>
        </div>
      </VuePerfectScrollbar>
    </v-container>
  </div>
</template>
<script>
import histories from "@/assets/test/history.json";
import VuePerfectScrollbar from "vue-perfect-scrollbar";
import "vue2-perfect-scrollbar/dist/vue2-perfect-scrollbar.css";

export default {
  data() {
    return {
      histories: histories,
      maxScrollbarLength: 60
    };
  },

  components: {
    VuePerfectScrollbar,
  },
};
</script>
<style src="@/style/common.css" scoped></style>
<style lang="css" scoped>
.GS_history_header {
  width: 100%;
  padding: 5px;
  display: flex;
  justify-content: flex-start;
  font-size: 0.8rem;
  text-align: left;
}

.GS_history_comment {
  width: 100%;
  padding: 5px 0 0 8px;
  text-align: left;
  font-size: 1rem;
  color: #666;
}
.GS_history_expression {
  width: 100%;
  padding: 5px 0 0 10px;
  text-align: left;
  font-size: 0.8rem;
  color: #666;
  font-style: italic;
}
.ps {
  height: 400px;
  border: 1px solid #bbb;
}
</style>