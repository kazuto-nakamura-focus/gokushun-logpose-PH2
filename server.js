const express = require("express");
const path = require("path");
const { createProxyMiddleware } = require("http-proxy-middleware");

const serveStatic = require("serve-static");
const history = require("connect-history-api-fallback");

const { spawn, spawnSync } = require("child_process");

const app = express();

//バックエンドアプリのJARファイルパス
const BACKEND_JAR_PATH =
  "./backend/jar/logpose-ph2-api-0.0.1-SNAPSHOT.jar";
const BATCH_JAR_PATH =
  "./batch/jar/logpose-ph2-batch-0.0.1-SNAPSHOT.jar";

//バックエンドアプリを起動
const backendProcess = spawn("java", ["-jar", BACKEND_JAR_PATH, "--spring.profiles.active=prd",]);
//const batchProcess = spawn("java", ["-jar", BATCH_JAR_PATH, "--spring.profiles.active=prd", "-", "true", "-"]);

//バックエンド起動時のコールバック関数登録
backendProcess.stdout.on("data", (data) => {
  console.log(`Backend Process:${data}`);
});

backendProcess.stderr.on("data", (data) => {
  console.log(`Backend Error:${data}`);
});

backendProcess.on("close", (code) => {
  console.log(`Backend process exited with code ${code}`);
});


//パスに/apiが付くURLは、バックエンドURLに設定
app.use(
  "/api",
  createProxyMiddleware({
    target: "http://localhost:9000",
    changeOrigin: true,
  })
);

app.use(history());
app.use(serveStatic(__dirname + "/frontend/dist"));

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log("server started " + port);
});

process.on("exit", () => {
  backendProcess.kill();
});
