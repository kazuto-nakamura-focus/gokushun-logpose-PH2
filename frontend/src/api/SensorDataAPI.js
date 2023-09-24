import axios from "@/lib/axiosHooks";
// センサーリストのデータ取得
const useSensoreList = (deviceId) => {
    const config = {
        params: {
            deviceId,
        },
    }
    return axios.get("/sensor/list", config);
};
//  センサーデータの取得
const useSensoreData = (sensorId, startDate, endDate, type, hour) => {
    const config = {
        params: {
            sensorId,
            startDate,
            endDate,
            type,
            hour
        },
    }
    return axios.get("/sensor/graph", config);
};
export {
    useSensoreList,
    useSensoreData,
  };