## Decorator
Cambiamos el calcularPaginas() original:

    public int calcularPaginas() {
      int paginas = (datos.size() / 10) + 1; // 10 items por página base
      
      // TODO: Decorator
      if (conEncabezado) paginas += 1;
      if (conPieDePagina) paginas += 0; // no agrega páginas, solo espacio
      if (conIndice) paginas += 2;
      if (conGraficos) paginas += 3;
      if (conResumenEjecutivo) paginas += 1;
      
      return paginas;
    }

Para hacer un calcularTotalPaginas() que utilice los **decoradores**:

    public int calcularTotalPaginas() {
      IReporte reporteFinal = this;
      if (conEncabezado) {
        reporteFinal = new ReporteConEncabezado(reporteFinal);
      }
      
      if (conPieDePagina){
        reporteFinal = new ReporteConPieDePagina(reporteFinal);
      }
      
      if (conIndice) {
        reporteFinal = new ReporteConIndice(reporteFinal);
      }
      
      if(conGraficos){
        reporteFinal = new ReporteConGraficos(reporteFinal);
      }
      
      if (conResumenEjecutivo){
        reporteFinal = new ReporteConResumenEjecutivo(reporteFinal);
      }
      
      return reporteFinal.calcularPaginas();
      
    }

Estructura de Decorators:
- Agrego IReporte para manejar de igual forma a todos los tipos de reporte

        public interface IReporte {
            int calcularPaginas();
        }

- Ahora la clase Reporte implementa esa interfaz e implementa el método:

      static class Reporte implements IReporte {

        @Override
        public int calcularPaginas() {
            int paginas = (datos.size() / 10) + 1; // 10 items por página base
            return paginas;
        }
      
      }

- Implementar **decoradores**
    - Decorator base

            static class ReporteDecorator implements IReporte {
                protected IReporte reporte;

                public ReporteDecorator(IReporte reporte){
                this.reporte = reporte;
                }

                @Override
                public int calcularPaginas(){
                return this.reporte.calcularPaginas();
                }

            }

    - Decoradores concretos

            static class ReporteConEncabezado extends ReporteDecorator {
                public ReporteConEncabezado(IReporte reporte){
                super(reporte);
                }

                @Override
                public int calcularPaginas(){
                return this.reporte.calcularPaginas() + 1;
                }

            }

            static class ReporteConPieDePagina extends ReporteDecorator {
                public ReporteConPieDePagina(IReporte reporte){
                super(reporte);
                }

                @Override
                public int calcularPaginas(){
                return this.reporte.calcularPaginas(); // No le agregaba páginas
                }
            }

## Builder

Código original:

    // Crear reporte básico
    Reporte reporte = new Reporte("Reporte Anual 2024", datos);
    reporte.agregarEncabezado(true);
    reporte.agregarPieDePagina(true);
    reporte.agregarIndice(true);
    reporte.agregarGraficos(true);
    reporte.agregarResumenEjecutivo(true);

Código final:

    Reporte reporte = new ReporteBuilder().
      conTituloYDatos("Reporte Anual 2024", datos)
      .addEncabezado()
      .addPieDePagina()
      .addIndice()
      .addGraficos()
      .addResumenEjecutivo()
      .build();


Habiendo implementado el **builder**:

    static class ReporteBuilder {
        private String titulo;
        private List<String> datos;
        private boolean conEncabezado = false;
        private boolean conPieDePagina = false;
        private boolean conIndice = false;
        private boolean conGraficos = false;
        private boolean conResumenEjecutivo = false;
        
        public ReporteBuilder conTituloYDatos(String titulo, List<String> datos){
            this.titulo = titulo;
            this.datos = datos;
            return this;
        }

        public ReporteBuilder addEncabezado(){
            this.conEncabezado = true;
            return this;
        }

        public ReporteBuilder addPieDePagina(){
            this.conPieDePagina = true;
            return this;
        }

        public ReporteBuilder addIndice(){
            this.conIndice = true;
            return this;
        }

        public ReporteBuilder addGraficos(){
            this.conGraficos = true;
            return this;
        }

        public ReporteBuilder addResumenEjecutivo(){
            this.conResumenEjecutivo = true;
            return this;
        }

        public Reporte build() {
            Reporte reporte = new Reporte(titulo, datos);
            reporte.agregarEncabezado(conEncabezado);
            reporte.agregarPieDePagina(conPieDePagina);
            reporte.agregarIndice(conIndice);
            reporte.agregarGraficos(conGraficos);
            reporte.agregarResumenEjecutivo(conResumenEjecutivo);
            return reporte;
        }
    }

## Strategy