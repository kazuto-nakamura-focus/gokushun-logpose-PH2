<!--着果量着果負担表示画面-->
<template>
  <div>
    <v-card width="auto">
      <v-container width="auto">
        <v-row>
          <v-col cols="6">
            <v-select
              v-model="baseDevice"
              :items="devices"
              item-text="text"
              item-value="id"
              width="300"
              dense
              label="基準デバイス選択"
              @change="handleChangeDevice"
              return-object
              height="45px"
              style="font-size: 10pt"
            ></v-select>
          </v-col>
          <v-col cols="2">
            <v-text-field
              v-model="baseDate"
              label="基準日"
              disabled
              style="font-size: 9pt"
            ></v-text-field>
          </v-col>
          <v-col cols="3">
            <v-text-field
              v-model="brand"
              label="品種"
              disabled
              style="font-size: 9pt"
            ></v-text-field>
          </v-col>
        </v-row>
      </v-container>
      <v-data-table
        min-width="960px"
        :headers="headers"
        :items="dataList"
        :item-class="itemClass"
        @pagination="onPaginationUpdate"
        hide-default-header
      >
        <template v-slot:header="{ props }">
          <thead>
            <tr>
              <th
                v-for="header in props.headers"
                :key="header.value"
                class="header"
              >
                <div
                  v-if="canGraph(header.value)"
                  style="width: 100%; text-align: cener; margin-bottom: 10px"
                >
                  <!--   <v-btn height="20px" @click="showGraph(header)"
                    ><span style="font-size: 9pt">グラフ表示</span></v-btn
                  >-->
                </div>
                <div
                  v-if="canSort(header.value)"
                  style="width: 100%; text-align: cener; margin-bottom: 10px"
                >
                  <v-btn height="20px" @click="sortList(header)"
                    ><span style="font-size: 9pt">ソート</span></v-btn
                  >
                </div>
                <div
                  style="
                    height: 70px;
                    padding: 0;
                    margin: 0;
                    text-align: left;
                    font-weight: 550;
                  "
                >
                  {{ header.text.split(";")[0] }} <br />{{
                    header.text.split(";")[1]
                  }}<br />{{ header.text.split(";")[2] }}
                </div>
                <div style="text-align: center; margin: 4px">
                  {{ header.text.split(";")[3] }}
                </div>
              </th>
            </tr>
          </thead>
        </template>
        <template v-slot:[`item.name`]="{ item }">
          <td
            style="font-size: 9pt; padding: 2px; border-right: 1px dotted #ccc"
          >
            {{ item.name }} [{{ item.year }}]
            <div style="padding: 3px; margin: 0">実測日 : {{ item.date }}</div>
          </td>
        </template>

        <template v-slot:[`item.date`]="{ item }">
          <td style="font-size: 9pt; padding: 4px">{{ item.date }}</td>
        </template>
        <template v-slot:[`item.harvestCrownLeafArea`]="{ item }">
          <td style="font-size: 9pt; padding: 4px; text-align: right">
            <span
              v-bind:class="
                check('harvestCrownLeafArea', item.harvestCrownLeafArea)
              "
              >{{ item.harvestCrownLeafArea }}<br />{{
                showDiff("harvestCrownLeafArea", item)
              }}</span
            >
          </td>
        </template>
        <template
          v-slot:[`item.culminatedCrownPhotoSynthesysAmount`]="{ item }"
        >
          <td style="font-size: 9pt; padding: 4px; text-align: right">
            <span
              v-bind:class="
                check(
                  'culminatedCrownPhotoSynthesysAmount',
                  item.culminatedCrownPhotoSynthesysAmount
                )
              "
              >{{ item.culminatedCrownPhotoSynthesysAmount }}<br />{{
                showDiff("culminatedCrownPhotoSynthesysAmount", item)
              }}</span
            >
          </td>
        </template>
        <template v-slot:[`item.bearingWeight`]="{ item }">
          <td style="font-size: 9pt; padding: 4px; text-align: right">
            <span v-bind:class="check('bearingWeight', item.bearingWeight)"
              >{{ item.bearingWeight }}<br />{{
                showDiff("bearingWeight", item)
              }}</span
            >
          </td>
        </template>
        <template v-slot:[`item.bearingPerPhotoSynthesys`]="{ item }">
          <td style="font-size: 9pt; padding: 4px; text-align: right">
            <span
              v-bind:class="
                check('bearingPerPhotoSynthesys', item.bearingPerPhotoSynthesys)
              "
              >{{ item.bearingPerPhotoSynthesys }}<br />{{
                showDiff("bearingPerPhotoSynthesys", item)
              }}</span
            >
          </td>
        </template>
        <template v-slot:[`item.bearingCount`]="{ item }">
          <td style="font-size: 9pt; padding: 4px; text-align: right">
            <span v-bind:class="check('bearingCount', item.bearingCount)"
              >{{ item.bearingCount }}<br />{{
                showDiff("bearingCount", item)
              }}</span
            >
          </td>
        </template>
        <!--       <template v-slot:[`item.name`]="{ item }">
        <td style="font-size:9pt">{{ item.name }}</td>
      </template>
            <template v-slot:[`item.date`]="{ item }">
        <td style="font-size:9pt">{{ item.date }}</td>
      </template> -->
      </v-data-table>
    </v-card>
    <v-dialog v-model="isDiffDialog" width="70%" maxHeight="400px">
      <ph-2-diff-graph ref="refph2DiffGraph"></ph-2-diff-graph>
    </v-dialog>
  </div>
</template>
  
<script>
import moment from "moment";
import { useFruitDetails } from "@/api/TopStateGrowth/index";
import { useDeviceShortList } from "@/api/ManagementScreenTop/MSDevice/index";
import ph2DiffGraph from "@/components-v1/FruitBearing/比較グラフ.vue";

function createId(id, year) {
  return id + "+" + year;
}
function createDiff(value, base) {
  let diff = Math.round((value - base) * 100) / 100;
  return "(" + diff + ")";
}
export default {
  data() {
    return {
      baseDevice: createId(1221, moment().year()),
      selectedDevice: null,
      selectedBase: null,
      baseDate: null,
      brand: null,
      devices: [],
      isDiffDialog: false,
      baseObject: null,
      headerSortMap: new Map(),
      headers: [
        { text: "", value: "name", width: 180, align: "center" },
        {
          text: ";収穫時樹冠葉面積;;(m^2)",
          value: "harvestCrownLeafArea",
          width: 156,
          align: "right",
        },
        {
          text: ";積算樹冠光合成量;;(kgCO2vine^-1)",
          value: "culminatedCrownPhotoSynthesysAmount",
          width: 156,
          align: "right",
        },
        {
          text: "着果負担;(果実総重量;/収穫時樹冠葉面積);(g/m^2)",
          value: "bearingWeight",
          width: 156,
          align: "right",
        },
        {
          text: "積算樹冠光合成量あたりの着果量;(果実総重量;/積算樹冠光合成量);(g/kgCO2 vine^-1)",
          value: "bearingPerPhotoSynthesys",
          width: 156,
          align: "right",
        },
        {
          text: "実測着果数;/収穫時樹冠葉面積;;(房数/m^2)",
          value: "bearingCount",
          width: 156,
          align: "right",
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
  components: {
    ph2DiffGraph,
  },
  mounted() {
    //* ============================================
    // 基準デバイス選択メニューを作成する
    //* ============================================
    useDeviceShortList()
      .then((response) => {
        const { status, message, data } = response["data"];
        if (status === 0) {
          for (const row of data) {
            let item = {
              id: createId(row.id, row.year),
              deviceId: row.id,
              year: row.year,
              text: row.fieldName + "|" + row.name + " [" + row.year + "]", // セレクタで表示する名前
              name: row.fieldName + "|" + row.name, // テーブル内で表示する名前、yearと併用
              baseDate: row.baseDate,
              brand: row.brand,
            };
            this.devices.push(item);
            // * 基準デバイスの場合、データを取得する
            if (item.id == this.baseDevice) {
              this.baseDate = row.baseDate;
              this.brand = row.brand;
              this.callUseFruitDetailsAPI(item.deviceId, item.year, item.name);
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
        selectedItems.selectedDevice.name;

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
            if (id != this.baseDevice) {
              this.selectedDevice = id;
              this.dataList.splice(1, 0, data);
            } else {
              this.baseObject = data;
              this.selectedBase = id;
              this.dataList.unshift(data);
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
    //* ============================================
    // キーデバイスの背景色を変更する
    //* ============================================
    itemClass(item) {
      if (item.id == this.baseDevice) {
        return "first-row";
      } else if (item.id == this.selectedDevice) {
        return "second-row";
      } else return "";
    },

    onPaginationUpdate(newPagination) {
      // Rows per pageの変更を取得
      if (newPagination.itemsPerPage !== this.pagination.itemsPerPage) {
        this.pagination = newPagination;
      }
    },
    handleChangeDevice(item) {
      let i = 0;
      console.log(this.baseDevice);
      for (const old of this.dataList) {
        if (old.id == this.selectedBase) {
          this.dataList.splice(i, 1);
          break;
        }
        i++;
      }
      this.baseDate = item.baseDate;
      this.brand = item.brand;
      this.baseDevice = item.id;
      this.callUseFruitDetailsAPI(item.deviceId, item.year, item.name);
    },
    check(name, value) {
      if ("-" == value) return "equal";
      let base = this.baseObject[name];
      if (base > value) return "less";
      else if (base < value) return "more";
      else return "equal";
    },
    showDiff(name, item) {
      if (item !== this.baseObject) {
        if (item[name] != "-" && this.baseObject[name] != "-")
          return createDiff(item[name], this.baseObject[name]);
      }
    },
    canGraph(name) {
      return this.dataList.length > 1 && name != "name";
    },
    canSort(name) {
      return this.dataList.length > 2 && name != "name";
    },
    showGraph(item) {
      this.isDiffDialog = true;
      let title = item.text.replace(/;/g, "");
      this.$nextTick(function () {
        this.$refs.refph2DiffGraph.initialize(
          title,
          item.value,
          this.baseObject[item.value],
          this.dataList
        );
      });
    },
    sortList(item) {
      console.log(item);
      let top = this.dataList[0];
      this.dataList.splice(0, 1);
      for (const data of this.dataList) {
        if (data[item.value] == "-") {
          data[item.value] = 0;
        }
      }
      if (
        this.headerSortMap[item.value] === undefined ||
        this.headerSortMap[item.value] == "asc"
      ) {
        this.headerSortMap[item.value] = new Object();
        this.headerSortMap[item.value] = "desc";
        this.dataList.sort((a, b) => a[item.value] - b[item.value]);
      } else {
        this.headerSortMap[item.value] = "asc";
        this.dataList.sort((a, b) => b[item.value] - a[item.value]);
      }

      this.dataList.unshift(top);
      for (const data of this.dataList) {
        if (data[item.value] == 0) {
          data[item.value] = "-";
        }
      }
    },
  },
};
</script>
  
<style lang="css">
.first-row {
  background-color: #fcfce0;
  color: #000;
}
.second-row {
  color: #058d1e;
  font-weight: 550;
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
.header {
  font-size: 8pt;
  text-align: left;
  width: 156px;
  border-left: #ccc dotted 1px;
}
.equal {
  color: #000;
}
.more {
  color: #44f;
}
.less {
  color: #f44;
}
</style>
  