import http from "k6/http";
import { check, sleep } from "k6";

const baseUrl = __ENV.BASE_URL || "http://localhost:30081";
const username = __ENV.USERNAME || "admin";
const password = __ENV.PASSWORD || "admin123";

export const options = {
  stages: [
    { duration: "30s", target: 10 },
    { duration: "3m", target: 10 },
    { duration: "30s", target: 0 }
  ],
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<2000"]
  }
};

export function setup() {
  const loginResponse = http.post(
    `${baseUrl}/auth/login`,
    JSON.stringify({ username, password }),
    { headers: { "Content-Type": "application/json" } }
  );

  check(loginResponse, {
    "login returned 200": (response) => response.status === 200
  });

  const payload = loginResponse.json();
  if (!payload || !payload.accessToken) {
    throw new Error("Nao foi possivel obter o token JWT para o stress test.");
  }

  return { token: payload.accessToken };
}

export default function (data) {
  const params = {
    headers: {
      Authorization: `Bearer ${data.token}`
    }
  };

  const clientes = http.get(`${baseUrl}/api/clientes?page=0&size=5`, params);
  check(clientes, {
    "clientes returned 200": (response) => response.status === 200
  });

  const tarefas = http.get(`${baseUrl}/api/tarefas?page=0&size=5`, params);
  check(tarefas, {
    "tarefas returned 200": (response) => response.status === 200
  });

  sleep(1);
}
