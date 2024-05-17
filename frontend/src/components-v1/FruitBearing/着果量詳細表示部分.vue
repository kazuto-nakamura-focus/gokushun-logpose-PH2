<!--着果量着果負担表示画面-->
<template>
  <v-card>
    <v-container>
      <v-row>
        <v-col cols="6">
          <v-select
            v-model="baseDevice"
            :items="devices"
            item-text="name"
            item-value="id"
            width="300"
            dense
            label="基準デバイス"
            @change="handleChangeDevice"
            return-object
            height="45px"
            style="font-size:10pt;"
          ></v-select>
        </v-col>
        <v-col cols="3">
          <v-text-field v-model="baseDate" label="基準日" disabled style="font-size:9pt;"></v-text-field>
        </v-col>
        <v-col cols="3">
          <v-text-field v-model="brand" label="品種" disabled style="font-size:9pt;"></v-text-field>
        </v-col>
      </v-row>
    </v-container>
    <v-data-table
      width="100%"
      min-width="800px"
      :headers="headers"
      :items="dataList"
      :item-class="itemClass"
      :disable-sort="!sortable"
      @pagination="onPaginationUpdate"
    > 
      <template v-slot:[`item.name`]="{ item }">
        <td style="font-size:9pt;padding:2px;border-right:1px dotted #ccc;">{{ item.name }}</td>
      </template>

      <template v-slot:[`item.date`]="{ item }">
        <td style="font-size:9pt;padding:4px">{{ item.date }}</td>
      </template>
      <!--       <template v-slot:[`item.name`]="{ item }">
        <td style="font-size:9pt">{{ item.name }}</td>
      </template>
            <template v-slot:[`item.date`]="{ item }">
        <td style="font-size:9pt">{{ item.date }}</td>
      </template> -->
    </v-data-table>
  </v-card>
</template>
  
  <script>
import { useFruitDetails } from "@/api/TopStateGrowth/index";
import { useDeviceShortList } from "@/api/ManagementScreenTop/MSDevice/index";

function createId(id, year){
  return id + "+" + year;
}
export default {
  data() {
    return {
      baseDevice: createId(53, 2023),
      sortable: true,
      baseDate: null,
      brand: null,
      devices: [],
      headers: [
        { text: "", value: "name", width: 180 },
        { text: "実測日", value: "date", width: 100 },
        { text: "収穫時樹冠葉面積(m^2)", value: "harvestCrownLeafArea" },
        {
          text: "積算樹冠光合成量(kgCO2vine^-1)",
          value: "culminatedCrownPhotoSynthesysAmount"
        },
        {
          text: "着果負担（果実総重量/収穫時樹冠葉面積）(g/m^2)",
          value: "bearingWeight"
        },
        {
          text: "積算樹冠光合成量あたりの着果量（果実総重量/積算樹冠光合成量）(g/kgCO2 vine^-1)",
          value: "bearingPerPhotoSynthesys"
        },
        {
          text: "実測着果数/収穫時樹冠葉面積(房数/m^2)",
          value: "bearingCount"
        },
      ],
      dataList: [],
      pagination: {
        page: 1,
        itemsPerPage: 10, // 初期のRows per pageの値
        totalItems: 0,
      },
    };
  },

  mounted() {
    useDeviceShortList()
      .then((response) => {
        const { status, message, data } = response["data"];
        if (status === 0) {
          for (const row of data) {
            let item = {
              id : createId(row.id, row.year),
              deviceId : row.id,
              year: row.year,
              name: row.fieldName + "|" + row.name + "|" + row.year,
              baseDate: row.baseDate,
              brand: row.brand,
            };
            this.devices.push(item);
            if (item.id == this.baseDevice) {
              this.baseDate = row.baseDate;
              this.brand = row.brand;
              this.callUseFruitDetailsAPI(item.deviceId,item.year,item.name);
              this.$emit("mounted");
            }
          }

        } else {
          throw new Error(message);
        }
      })
      .catch((e) => {
        alert("データの取得ができませんでした。");
        //失敗時
        console.log(e);
      });
    
  },
  methods: {
    //* ============================================
    // 指定デバイスの指定年度の着果量負担の詳細を表示する
    //* ============================================
    initialize: function (selectedItems) {
      // * 表示名
      let name =
        selectedItems.selectedField.name +
        "|" +
        selectedItems.selectedDevice.name +
        "|" +
        selectedItems.selectedYear.id;

      // * データの取得
      this.callUseFruitDetailsAPI(
        selectedItems.selectedDevice.id,
        selectedItems.selectedYear.id,
        name
      );
    },
    //* ============================================
    // 着果量負担の詳細を取得する
    //* ============================================
    callUseFruitDetailsAPI(deviceId, year, name) {
      console.log("aaa");
      useFruitDetails(deviceId, year)
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status === 0) {
            // * idの設定
            let id = createId(deviceId, year);
            data.deviceId = new Number(data.id);
            data.id = id;
            // * 名前の検索
            let i = 0;
            for (const item of this.dataList) {
              if (item.id == id) {
                this.dataList.splice(i, 1);
                break;
              }
              i++;
            }

            // * 値の設定
            data.name = new String(name);
            if (data.date == null) {
              data.date = "なし";
            }

            if (data.harvestCrownLeafArea == 0) {
              data.harvestCrownLeafArea = "-";
            } else {
              data.harvestCrownLeafArea =
                Math.round(data.harvestCrownLeafArea * 100) / 100;
            }

            if (data.culminatedCrownPhotoSynthesysAmount == 0) {
              data.culminatedCrownPhotoSynthesysAmount = "-";
            } else {
              data.culminatedCrownPhotoSynthesysAmount =
                Math.round(data.culminatedCrownPhotoSynthesysAmount * 100) /
                100;
            }

            if (data.bearingWeight == 0) {
              data.bearingWeight = "-";
            } else {
              data.bearingWeight = Math.round(data.bearingWeight * 100) / 100;
            }

            if (data.bearingPerPhotoSynthesys == 0) {
              data.bearingPerPhotoSynthesys = "-";
            } else {
              data.bearingPerPhotoSynthesys =
                Math.round(data.bearingPerPhotoSynthesys * 100) / 100;
            }

            if (data.bearingCount == 0) {
              data.bearingCount = "-";
            } else {
              data.bearingCount = Math.round(data.bearingCount * 100) / 100;
            }
            // * デバイスのキー設定と表示設定
            if(id != this.baseDevice){
              this.dataList.splice(1, 0, data);
            } else {
              this.dataList.unshift(data);
            }
            this.sortable = true;
          } else {
            throw new Error(message);
          }
        })
        .catch((e) => {
          alert("データの取得ができませんでした。");
          //失敗時
          console.log(e);
        });
    },
    //* ============================================
    // キーデバイスの背景色を変更する
    //* ============================================
    itemClass(item) {
      return item.id == this.baseDevice ? "first-row" : "";
    },

    onPaginationUpdate(newPagination) {
      // Rows per pageの変更を取得
      if (newPagination.itemsPerPage !== this.pagination.itemsPerPage) {
        this.pagination = newPagination;
      }
    },
    handleChangeDevice(item) {
      this.sortable = false;
      this.baseDate = item.baseDate;
      this.brand = item.brand;
      this.baseDevice = item.id;
      this.callUseFruitDetailsAPI(item.deviceId,item.year,item.name);
    },
  },
};
</script>
  
<style lang="css">
.first-row {
  background-color: #f4fce0;
  color: #000;
}
.wrapper {
  display: -webkit-flex;
  display: -moz-flex;
  display: -ms-flex;
  display: -o-flex;
  display: flex;
  flex-wrap: wrap;
}
.ph2_data_title {
  width: 100%;
  font-size: 9pt;
  border-bottom: 1px solid #ccc;
  padding: 3px 0;
}
</style>
  