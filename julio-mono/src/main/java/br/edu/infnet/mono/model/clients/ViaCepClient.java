package br.edu.infnet.mono.model.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "viacep", url = "${viacep.url}")
public interface ViaCepClient {
	
	@GetMapping("/{cep}/json")
	ViaCepResponse buscarEnderecoPorCep(@PathVariable("cep") String cep);
	
    class ViaCepResponse {
    	
    	private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;
        private String localidade;
        private String uf;
        private boolean erro;
    	
    	
        public String getCep() {
			return cep;
		}
		public void setCep(String cep) {
			this.cep = cep;
		}
		public String getLogradouro() {
			return logradouro;
		}
		public void setLogradouro(String logradouro) {
			this.logradouro = logradouro;
		}
		public String getComplemento() {
			return complemento;
		}
		public void setComplemento(String complemento) {
			this.complemento = complemento;
		}
		public String getBairro() {
			return bairro;
		}
		public void setBairro(String bairro) {
			this.bairro = bairro;
		}
		public String getLocalidade() {
			return localidade;
		}
		public void setLocalidade(String localidade) {
			this.localidade = localidade;
		}
		public String getUf() {
			return uf;
		}
		public void setUf(String uf) {
			this.uf = uf;
		}
		public boolean isErro() {
			return erro;
		}
		public void setErro(boolean erro) {
			this.erro = erro;
		}
	
        
 
    }
    
    
}