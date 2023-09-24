import axios from "@/lib/axiosHooks";

const useViewDataList = (params) => {
  const config = {
    params: params,
  };
  //確認必要！！：URLの指定をお願いします。
  return axios.get("/fields", config); 
};


// タイプを指定して、リクエスト
// const params = {date: "2023-01-01", deviceId:"123123"}
// useViewDataList(params).then((res)=> {
//     //正常処理
//     //ここで、データを受け取り、一覧の内容を変数にセット
//     // src/components/ViewDataList/index.vueを確認したところ、一覧を格納するオブジェクト名は、以下のとおりである。
//     // masterRawData,

//     //取得する一覧のオブジェクトに、キーの値を基づき、src/assets/test/masterRawDataListHeader.jsonを編集してそのまま使用可能
//     //一覧を取得後、masterRawDataに格納することで、表示できる。


// }).catch((err) => {
//     //エラー処理

// });