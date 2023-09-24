import axios from "@/lib/axiosHooks";

//検知データ圃場別取得
const useSum = (detectId) => {
  const config = {
    params: {
      detectId,
    },
  };
  return axios.get("/sum", config);
};

export { useSum };
