let generateConfig = (ip, port) => `${ip}:${port}`;

export class HTTP_Config {
  static SERVER_IP = "127.0.0.1";
  static SERVER_PORT = "8080";

  static SERVER_CONFIG = generateConfig(HTTP_Config.SERVER_IP, HTTP_Config.SERVER_PORT);
  static MASTER_DATABASE = generateConfig(HTTP_Config.SERVER_IP, "3306");
  static CACHE_DATABASE = generateConfig(HTTP_Config.SERVER_IP, "6379");
}
