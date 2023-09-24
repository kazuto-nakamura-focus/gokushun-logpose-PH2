import axios from "@/lib/axiosHooks";
// ID001
// 全圃場サマリーデータ取得
const useFields = () => {
  const config = {
    params: {},
  };
  return axios.get("/fields", config); // List<DataSummaryDTO>
};
// 検知データ圃場別取得
const useSum = (contentId) => {
  const config = {
    params: {contentId : contentId},
  };
  return axios.get("/sum", config); // List<FieldData>
};
// モデル選択情報取得
const useModels = () => {
  const config = {
    params: {},
  };
  return axios.get("/models", config); // List<FieldData>
};
// 生データ取得
const useRawData = (deviceId, startDate, endDate) => {
  const config = {
    params: {deviceId, startDate, endDate},
  };
  return axios.get("/rawData", config); // List<FieldData>
};
export { useFields, useSum, useModels, useRawData };
