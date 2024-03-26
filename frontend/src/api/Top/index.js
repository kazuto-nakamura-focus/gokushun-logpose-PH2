import axios from "@/lib/axiosHooks";
axios.interceptors.response.use(
  (response) => response, // 成功時のresponseはそのまま返す
  (error) => {
    console.log(error);
    window.location.href =
      "https://id.heroku.com/oauth/authorize?client_id=2faedc8a-eeb0-4956-a93d-0c7c82181bf8&response_type=code&scope=identity&state=shufvel9872";

  }
);
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
    params: { contentId: contentId },
  };
  return axios.get("/sum", config); // List<FieldData>
};
// モデル選択情報取得
const useModels = (isModel) => {
  const config = {
    params: { isModel },
  };
  return axios.get("/models", config); // List<FieldData>
};
// 生データ取得
const useRawData = (deviceId, startDate, endDate) => {
  const config = {
    params: { deviceId, startDate, endDate },
  };
  return axios.get("/rawData", config); // List<FieldData>
};
export { useFields, useSum, useModels, useRawData };
