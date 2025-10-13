/*==============================================================*/
/* DBMS name:      PostgreSQL 9.x                               */
/* Created on:     4/10/2025 11:42:28                           */
/*==============================================================*/


drop index ADQUIERE_FK;

drop index PARTICIPA_FK;

drop index COMPRA_PK;

drop table COMPRA;

drop index SE_COMPONE_EN_FK;

drop index DETALLEAMORTIZACION_PK;

drop table DETALLEAMORTIZACION;

drop index SE_REGISTRA_EN_FK;

drop index HISTORIAL_PK;

drop table HISTORIAL;

drop index RELATIONSHIP_5_FK;

drop index INTANGIBLE_PK;

drop table INTANGIBLE;

drop index PROVEEDOR_PK;

drop table PROVEEDOR;

drop index GENERA_FK;

drop index REPORTE_PK;

drop table REPORTE;

drop index USUARIO_PK;

drop table USUARIO;

/*==============================================================*/
/* Table: COMPRA                                                */
/*==============================================================*/
create table COMPRA (
   IDCOMPRA             VARCHAR(10)          not null,
   IDPROVEDOR           VARCHAR(10)          not null,
   IDINTANGIBLE         VARCHAR(10)          not null,
   MONTO                DECIMAL(10,2)        not null,
   FECHA_COMPRA         DATE                 not null,
   FECHA_VENCIMIENTO    DATE                 not null,
   constraint PK_COMPRA primary key (IDCOMPRA)
);

/*==============================================================*/
/* Index: COMPRA_PK                                             */
/*==============================================================*/
create unique index COMPRA_PK on COMPRA (
IDCOMPRA
);

/*==============================================================*/
/* Index: PARTICIPA_FK                                          */
/*==============================================================*/
create  index PARTICIPA_FK on COMPRA (
IDPROVEDOR
);

/*==============================================================*/
/* Index: ADQUIERE_FK                                           */
/*==============================================================*/
create  index ADQUIERE_FK on COMPRA (
IDINTANGIBLE
);

/*==============================================================*/
/* Table: DETALLEAMORTIZACION                                   */
/*==============================================================*/
create table DETALLEAMORTIZACION (
   IDDETALLE            VARCHAR(10)          not null,
   IDINTANGIBLE         VARCHAR(10)          not null,
   NUMERO_CUOTA         VARCHAR(10)          not null,
   MONTO                DECIMAL(10,2)        not null,
   AMORTIZACION_MENSUAL DECIMAL(10,2)        not null,
   AMORTIZACION_ANUAL   DECIMAL(10,2)        null,
   constraint PK_DETALLEAMORTIZACION primary key (IDDETALLE)
);

/*==============================================================*/
/* Index: DETALLEAMORTIZACION_PK                                */
/*==============================================================*/
create unique index DETALLEAMORTIZACION_PK on DETALLEAMORTIZACION (
IDDETALLE
);

/*==============================================================*/
/* Index: SE_COMPONE_EN_FK                                      */
/*==============================================================*/
create  index SE_COMPONE_EN_FK on DETALLEAMORTIZACION (
IDINTANGIBLE
);

/*==============================================================*/
/* Table: HISTORIAL                                             */
/*==============================================================*/
create table HISTORIAL (
   IDHISTORIAL          VARCHAR(10)          not null,
   IDCOMPRA             VARCHAR(10)          not null,
   constraint PK_HISTORIAL primary key (IDHISTORIAL)
);

/*==============================================================*/
/* Index: HISTORIAL_PK                                          */
/*==============================================================*/
create unique index HISTORIAL_PK on HISTORIAL (
IDHISTORIAL
);

/*==============================================================*/
/* Index: SE_REGISTRA_EN_FK                                     */
/*==============================================================*/
create  index SE_REGISTRA_EN_FK on HISTORIAL (
IDCOMPRA
);

/*==============================================================*/
/* Table: INTANGIBLE                                            */
/*==============================================================*/
create table INTANGIBLE (
   IDINTANGIBLE         VARCHAR(10)          not null,
   IDUSUARIO            VARCHAR(10)          not null,
   VERSION              VARCHAR(25)          not null,
   NOMBRE_PROVEEDOR     VARCHAR(25)          not null,
   TIPO_LICENCIA_       VARCHAR(25)          not null,
   CODIGO_              VARCHAR(25)          not null,
   COSTO                DECIMAL(10,2)        not null,
   VIDA_UTIL            VARCHAR(25)          not null,
   ESTADO_              VARCHAR(25)          not null,
   constraint PK_INTANGIBLE primary key (IDINTANGIBLE)
);

/*==============================================================*/
/* Index: INTANGIBLE_PK                                         */
/*==============================================================*/
create unique index INTANGIBLE_PK on INTANGIBLE (
IDINTANGIBLE
);

/*==============================================================*/
/* Index: RELATIONSHIP_5_FK                                     */
/*==============================================================*/
create  index RELATIONSHIP_5_FK on INTANGIBLE (
IDUSUARIO
);

/*==============================================================*/
/* Table: PROVEEDOR                                             */
/*==============================================================*/
create table PROVEEDOR (
   IDPROVEDOR           VARCHAR(10)          not null,
   NOMBRE_PROVEEDOR     VARCHAR(25)          not null,
   constraint PK_PROVEEDOR primary key (IDPROVEDOR)
);

/*==============================================================*/
/* Index: PROVEEDOR_PK                                          */
/*==============================================================*/
create unique index PROVEEDOR_PK on PROVEEDOR (
IDPROVEDOR
);

/*==============================================================*/
/* Table: REPORTE                                               */
/*==============================================================*/
create table REPORTE (
   IDREPORTE            VARCHAR(10)          not null,
   IDINTANGIBLE         VARCHAR(10)          not null,
   constraint PK_REPORTE primary key (IDREPORTE)
);

/*==============================================================*/
/* Index: REPORTE_PK                                            */
/*==============================================================*/
create unique index REPORTE_PK on REPORTE (
IDREPORTE
);

/*==============================================================*/
/* Index: GENERA_FK                                             */
/*==============================================================*/
create  index GENERA_FK on REPORTE (
IDINTANGIBLE
);

/*==============================================================*/
/* Table: USUARIO                                               */
/*==============================================================*/
create table USUARIO (
   IDUSUARIO            VARCHAR(10)          not null,
   CONTRASENA           VARCHAR(8)           not null,
   constraint PK_USUARIO primary key (IDUSUARIO)
);

/*==============================================================*/
/* Index: USUARIO_PK                                            */
/*==============================================================*/
create unique index USUARIO_PK on USUARIO (
IDUSUARIO
);

alter table COMPRA
   add constraint FK_COMPRA_ADQUIERE_INTANGIB foreign key (IDINTANGIBLE)
      references INTANGIBLE (IDINTANGIBLE)
      on delete restrict on update restrict;

alter table COMPRA
   add constraint FK_COMPRA_PARTICIPA_PROVEEDO foreign key (IDPROVEDOR)
      references PROVEEDOR (IDPROVEDOR)
      on delete restrict on update restrict;

alter table DETALLEAMORTIZACION
   add constraint FK_DETALLEA_SE_COMPON_INTANGIB foreign key (IDINTANGIBLE)
      references INTANGIBLE (IDINTANGIBLE)
      on delete restrict on update restrict;

alter table HISTORIAL
   add constraint FK_HISTORIA_SE_REGIST_COMPRA foreign key (IDCOMPRA)
      references COMPRA (IDCOMPRA)
      on delete restrict on update restrict;

alter table INTANGIBLE
   add constraint FK_INTANGIB_RELATIONS_USUARIO foreign key (IDUSUARIO)
      references USUARIO (IDUSUARIO)
      on delete restrict on update restrict;

alter table REPORTE
   add constraint FK_REPORTE_GENERA_INTANGIB foreign key (IDINTANGIBLE)
      references INTANGIBLE (IDINTANGIBLE)
      on delete restrict on update restrict;

