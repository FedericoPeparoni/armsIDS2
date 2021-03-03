--
-- PostgreSQL database dump
--

-- Dumped from database version 9.2.2
-- Dumped by pg_dump version 9.2.0
-- Started on 2013-07-12 18:15:24

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--SET search_path = public, pg_catalog;

SET default_with_oids = false;

--
-- TOC entry 408 (class 1259 OID 103708)
-- Name: smf_families; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smf_families (
    root_fea_db_name character varying(72) NOT NULL,
    family_name character varying(120) NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 407 (class 1259 OID 103706)
-- Name: SMF_FAMILIES_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMF_FAMILIES_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5150 (class 0 OID 0)
-- Dependencies: 407
-- Name: SMF_FAMILIES_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMF_FAMILIES_ID1_seq" OWNED BY smf_families.id1;


--
-- TOC entry 410 (class 1259 OID 103716)
-- Name: smf_filter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smf_filter (
    smf_filter_pk integer NOT NULL,
    fea_pk integer NOT NULL,
    filter_name text NOT NULL,
    filter_type character varying(80) NOT NULL,
    owner character varying(60) NOT NULL,
    orcl_where text NOT NULL,
    change_date timestamp without time zone,
    id1 integer NOT NULL
);


--
-- TOC entry 409 (class 1259 OID 103714)
-- Name: SMF_FILTER_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMF_FILTER_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5151 (class 0 OID 0)
-- Dependencies: 409
-- Name: SMF_FILTER_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMF_FILTER_ID1_seq" OWNED BY smf_filter.id1;


--
-- TOC entry 412 (class 1259 OID 103727)
-- Name: smu_db_check; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_db_check (
    id1 double precision NOT NULL,
    id2 double precision NOT NULL,
    id3 double precision NOT NULL,
    id4 integer NOT NULL
);


--
-- TOC entry 411 (class 1259 OID 103725)
-- Name: SMU_DB_CHECK_ID4_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_DB_CHECK_ID4_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5152 (class 0 OID 0)
-- Dependencies: 411
-- Name: SMU_DB_CHECK_ID4_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_DB_CHECK_ID4_seq" OWNED BY smu_db_check.id4;


--
-- TOC entry 416 (class 1259 OID 103743)
-- Name: smu_filter_cond; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_filter_cond (
    filter_pk double precision NOT NULL,
    seq integer NOT NULL,
    fea_pk integer NOT NULL,
    l_paren integer,
    attr_pk double precision NOT NULL,
    v_fea_pk integer,
    v_attr_pk double precision,
    r_paren integer,
    conj character varying(4),
    smu_where_grp_pk double precision,
    v_value character varying(80),
    v_alias character varying(4),
    oper integer NOT NULL,
    alias character varying(4),
    id1 integer NOT NULL
);


--
-- TOC entry 415 (class 1259 OID 103741)
-- Name: SMU_FILTER_COND_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_FILTER_COND_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5153 (class 0 OID 0)
-- Dependencies: 415
-- Name: SMU_FILTER_COND_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_FILTER_COND_ID1_seq" OWNED BY smu_filter_cond.id1;


--
-- TOC entry 414 (class 1259 OID 103735)
-- Name: smu_filter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_filter (
    filter_pk double precision NOT NULL,
    fea_pk integer NOT NULL,
    filter_name character varying(160) NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 413 (class 1259 OID 103733)
-- Name: SMU_FILTER_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_FILTER_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5154 (class 0 OID 0)
-- Dependencies: 413
-- Name: SMU_FILTER_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_FILTER_ID1_seq" OWNED BY smu_filter.id1;


--
-- TOC entry 418 (class 1259 OID 103751)
-- Name: smu_process; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_process (
    process_pk double precision NOT NULL,
    process_name text NOT NULL,
    process_key character varying(200) NOT NULL,
    process_group character varying(160) NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 417 (class 1259 OID 103749)
-- Name: SMU_PROCESS_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_PROCESS_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5155 (class 0 OID 0)
-- Dependencies: 417
-- Name: SMU_PROCESS_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_PROCESS_ID1_seq" OWNED BY smu_process.id1;


--
-- TOC entry 422 (class 1259 OID 103770)
-- Name: smu_role_filter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_role_filter (
    role_pk double precision NOT NULL,
    filter_pk double precision NOT NULL,
    access_rights integer NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 421 (class 1259 OID 103768)
-- Name: SMU_ROLE_FILTER_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_ROLE_FILTER_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5156 (class 0 OID 0)
-- Dependencies: 421
-- Name: SMU_ROLE_FILTER_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_ROLE_FILTER_ID1_seq" OWNED BY smu_role_filter.id1;


--
-- TOC entry 420 (class 1259 OID 103762)
-- Name: smu_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_role (
    role_pk double precision NOT NULL,
    role_name character varying(160) NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 419 (class 1259 OID 103760)
-- Name: SMU_ROLE_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_ROLE_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5157 (class 0 OID 0)
-- Dependencies: 419
-- Name: SMU_ROLE_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_ROLE_ID1_seq" OWNED BY smu_role.id1;


--
-- TOC entry 424 (class 1259 OID 103778)
-- Name: smu_role_process; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_role_process (
    role_pk double precision NOT NULL,
    process_pk double precision NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 423 (class 1259 OID 103776)
-- Name: SMU_ROLE_PROCESS_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_ROLE_PROCESS_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5158 (class 0 OID 0)
-- Dependencies: 423
-- Name: SMU_ROLE_PROCESS_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_ROLE_PROCESS_ID1_seq" OWNED BY smu_role_process.id1;


--
-- TOC entry 426 (class 1259 OID 103786)
-- Name: smu_role_source; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_role_source (
    role_pk double precision NOT NULL,
    source_pk double precision NOT NULL,
    access_rights integer NOT NULL,
    approve boolean DEFAULT true,
    id1 integer NOT NULL
);


--
-- TOC entry 425 (class 1259 OID 103784)
-- Name: SMU_ROLE_SOURCE_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_ROLE_SOURCE_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5159 (class 0 OID 0)
-- Dependencies: 425
-- Name: SMU_ROLE_SOURCE_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_ROLE_SOURCE_ID1_seq" OWNED BY smu_role_source.id1;


--
-- TOC entry 428 (class 1259 OID 103795)
-- Name: smu_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_user (
    user_pk double precision NOT NULL,
    user_name character varying(32) NOT NULL,
    initials character varying(12) NOT NULL,
    node_name character varying(80) NOT NULL,
    phone1 character varying(80),
    email text,
    phone2 character varying(80),
    full_name character varying(160) NOT NULL,
    sm_status character varying(4),
    sm_cancel_date timestamp without time zone,
    id1 integer NOT NULL
);


--
-- TOC entry 427 (class 1259 OID 103793)
-- Name: SMU_USER_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_USER_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5160 (class 0 OID 0)
-- Dependencies: 427
-- Name: SMU_USER_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_USER_ID1_seq" OWNED BY smu_user.id1;


--
-- TOC entry 430 (class 1259 OID 103806)
-- Name: smu_user_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE smu_user_role (
    user_pk double precision NOT NULL,
    role_pk double precision NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 429 (class 1259 OID 103804)
-- Name: SMU_USER_ROLE_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SMU_USER_ROLE_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5161 (class 0 OID 0)
-- Dependencies: 429
-- Name: SMU_USER_ROLE_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SMU_USER_ROLE_ID1_seq" OWNED BY smu_user_role.id1;


--
-- TOC entry 378 (class 1259 OID 103555)
-- Name: sm_attr_d; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_attr_d (
    attr_pk double precision NOT NULL,
    seq_nr integer NOT NULL,
    attr_d text NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 377 (class 1259 OID 103553)
-- Name: SM_ATTR_D_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_ATTR_D_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5162 (class 0 OID 0)
-- Dependencies: 377
-- Name: SM_ATTR_D_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_ATTR_D_ID1_seq" OWNED BY sm_attr_d.id1;


--
-- TOC entry 376 (class 1259 OID 103544)
-- Name: sm_attr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_attr (
    attr_pk double precision NOT NULL,
    fea_pk integer NOT NULL,
    doc_subsection integer NOT NULL,
    db_name character varying(72) NOT NULL,
    doc_name character varying(240) NOT NULL,
    attr_name character varying(84) NOT NULL,
    in_rdb character varying(4) NOT NULL,
    in_graphics character varying(4) NOT NULL,
    in_rules character varying(4) NOT NULL,
    attr_access character varying(4) NOT NULL,
    attr_security character varying(4) NOT NULL,
    attr_type character varying(4) NOT NULL,
    attr_length integer NOT NULL,
    lo_val double precision NOT NULL,
    hi_val double precision NOT NULL,
    key_level integer NOT NULL,
    key_type character varying(4) NOT NULL,
    which_table character varying(4) NOT NULL,
    code_pk integer NOT NULL,
    def_pk double precision NOT NULL,
    form_pos integer NOT NULL,
    attr_required character varying(4) NOT NULL,
    attr_case character varying(4) NOT NULL,
    doc_page integer NOT NULL,
    class integer NOT NULL,
    format_code double precision NOT NULL,
    default_val character varying(60) NOT NULL,
    display_char character varying(4) NOT NULL,
    log_exs character varying(4) NOT NULL,
    mod_date timestamp without time zone NOT NULL,
    mod_date_d timestamp without time zone NOT NULL,
    mod_date_l timestamp without time zone NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 375 (class 1259 OID 103542)
-- Name: SM_ATTR_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_ATTR_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5163 (class 0 OID 0)
-- Dependencies: 375
-- Name: SM_ATTR_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_ATTR_ID1_seq" OWNED BY sm_attr.id1;


--
-- TOC entry 380 (class 1259 OID 103566)
-- Name: sm_code; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_code (
    code_pk integer NOT NULL,
    code_desc text NOT NULL,
    code_len integer NOT NULL,
    code_display character varying(4) NOT NULL,
    app_code character varying(8) NOT NULL,
    mod_date timestamp without time zone NOT NULL,
    code_pk2 integer,
    id1 integer NOT NULL
);


--
-- TOC entry 379 (class 1259 OID 103564)
-- Name: SM_CODE_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_CODE_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5164 (class 0 OID 0)
-- Dependencies: 379
-- Name: SM_CODE_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_CODE_ID1_seq" OWNED BY sm_code.id1;


--
-- TOC entry 382 (class 1259 OID 103577)
-- Name: sm_code_v; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_code_v (
    code_pk integer NOT NULL,
    code_value text NOT NULL,
    value_desc text NOT NULL,
    value_desc2 text,
    value_desc3 text,
    value_desc4 text,
    value_desc5 text,
    parent_value text,
    id1 integer NOT NULL
);


--
-- TOC entry 381 (class 1259 OID 103575)
-- Name: SM_CODE_V_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_CODE_V_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5165 (class 0 OID 0)
-- Dependencies: 381
-- Name: SM_CODE_V_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_CODE_V_ID1_seq" OWNED BY sm_code_v.id1;


--
-- TOC entry 387 (class 1259 OID 103607)
-- Name: sm_fea_d; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_fea_d (
    fea_pk integer NOT NULL,
    seq_nr integer NOT NULL,
    fea_d text NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 386 (class 1259 OID 103605)
-- Name: SM_FEA_D_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_FEA_D_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5166 (class 0 OID 0)
-- Dependencies: 386
-- Name: SM_FEA_D_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_FEA_D_ID1_seq" OWNED BY sm_fea_d.id1;


--
-- TOC entry 385 (class 1259 OID 103599)
-- Name: sm_fea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_fea (
    fea_pk integer NOT NULL,
    fea_nr integer NOT NULL,
    db_name character varying(72) NOT NULL,
    doc_name character varying(160) NOT NULL,
    fea_name character varying(96) NOT NULL,
    rel_pk integer NOT NULL,
    in_rdb character varying(4) NOT NULL,
    in_graphics character varying(4) NOT NULL,
    fea_type character varying(4) NOT NULL,
    fea_rmks character varying(4) NOT NULL,
    fea_notes character varying(4) NOT NULL,
    fea_trace character varying(4) NOT NULL,
    doc_sect integer NOT NULL,
    fea_exs character varying(4) NOT NULL,
    mod_date timestamp without time zone NOT NULL,
    mod_date_d timestamp without time zone NOT NULL,
    mod_date_r timestamp without time zone NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 384 (class 1259 OID 103597)
-- Name: SM_FEA_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_FEA_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5167 (class 0 OID 0)
-- Dependencies: 384
-- Name: SM_FEA_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_FEA_ID1_seq" OWNED BY sm_fea.id1;


--
-- TOC entry 389 (class 1259 OID 103618)
-- Name: sm_format; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_format (
    format_code double precision NOT NULL,
    fmt_string character varying(120) NOT NULL,
    fmt_example character varying(120) NOT NULL,
    fmt_data_type character varying(4) NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 388 (class 1259 OID 103616)
-- Name: SM_FORMAT_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_FORMAT_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5168 (class 0 OID 0)
-- Dependencies: 388
-- Name: SM_FORMAT_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_FORMAT_ID1_seq" OWNED BY sm_format.id1;


--
-- TOC entry 393 (class 1259 OID 103634)
-- Name: sm_general; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_general (
    dbvers character varying(44),
    id1 integer NOT NULL
);


--
-- TOC entry 392 (class 1259 OID 103632)
-- Name: SM_GENERAL_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_GENERAL_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5169 (class 0 OID 0)
-- Dependencies: 392
-- Name: SM_GENERAL_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_GENERAL_ID1_seq" OWNED BY sm_general.id1;


--
-- TOC entry 391 (class 1259 OID 103626)
-- Name: sm_gen; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_gen (
    base_date timestamp without time zone NOT NULL,
    spec_header1 character varying(96) NOT NULL,
    spec_header2 character varying(96) NOT NULL,
    dbvers character varying(44),
    crc_mode character varying(4),
    id1 integer NOT NULL
);


--
-- TOC entry 390 (class 1259 OID 103624)
-- Name: SM_GEN_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_GEN_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5170 (class 0 OID 0)
-- Dependencies: 390
-- Name: SM_GEN_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_GEN_ID1_seq" OWNED BY sm_gen.id1;


--
-- TOC entry 395 (class 1259 OID 103642)
-- Name: sm_geom; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_geom (
    db_name character varying(120),
    geom_field character varying(120),
    geom_type character varying(4),
    lat_field character varying(100),
    lon_field character varying(100),
    req_fields text,
    depends_on text,
    db_proc text,
    id1 integer NOT NULL
);


--
-- TOC entry 394 (class 1259 OID 103640)
-- Name: SM_GEOM_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_GEOM_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5171 (class 0 OID 0)
-- Dependencies: 394
-- Name: SM_GEOM_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_GEOM_ID1_seq" OWNED BY sm_geom.id1;


--
-- TOC entry 397 (class 1259 OID 103653)
-- Name: sm_primary_keys; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_primary_keys (
    db_name character varying(72) NOT NULL,
    pk double precision NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 396 (class 1259 OID 103651)
-- Name: SM_PRIMARY_KEYS_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_PRIMARY_KEYS_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5172 (class 0 OID 0)
-- Dependencies: 396
-- Name: SM_PRIMARY_KEYS_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_PRIMARY_KEYS_ID1_seq" OWNED BY sm_primary_keys.id1;


--
-- TOC entry 399 (class 1259 OID 103661)
-- Name: sm_prod_setting; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_prod_setting (
    product_name text,
    setting_name text,
    setting_value text,
    id1 integer NOT NULL
);


--
-- TOC entry 398 (class 1259 OID 103659)
-- Name: SM_PROD_SETTING_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_PROD_SETTING_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5173 (class 0 OID 0)
-- Dependencies: 398
-- Name: SM_PROD_SETTING_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_PROD_SETTING_ID1_seq" OWNED BY sm_prod_setting.id1;


--
-- TOC entry 402 (class 1259 OID 103680)
-- Name: sm_relations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_relations (
    fea_pk integer NOT NULL,
    fea_db_name character varying(72) NOT NULL,
    relation_fea_pk integer NOT NULL,
    relation_db_name character varying(72) NOT NULL,
    relation_type character varying(4) NOT NULL,
    join_attr_pk double precision NOT NULL,
    fea_list_attr_pk double precision NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 401 (class 1259 OID 103678)
-- Name: SM_RELATIONS_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_RELATIONS_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5174 (class 0 OID 0)
-- Dependencies: 401
-- Name: SM_RELATIONS_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_RELATIONS_ID1_seq" OWNED BY sm_relations.id1;


--
-- TOC entry 404 (class 1259 OID 103688)
-- Name: sm_rolldate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_rolldate (
    lastrolldate timestamp without time zone NOT NULL,
    operator character varying(120) NOT NULL,
    id1 integer NOT NULL
);


--
-- TOC entry 403 (class 1259 OID 103686)
-- Name: SM_ROLLDATE_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_ROLLDATE_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5175 (class 0 OID 0)
-- Dependencies: 403
-- Name: SM_ROLLDATE_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_ROLLDATE_ID1_seq" OWNED BY sm_rolldate.id1;


--
-- TOC entry 406 (class 1259 OID 103696)
-- Name: sm_source; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_source (
    source_pk double precision NOT NULL,
    source character varying(64) NOT NULL,
    issue_date timestamp without time zone NOT NULL,
    sm_status character varying(4) NOT NULL,
    sm_cancel_date timestamp without time zone,
    src_title text NOT NULL,
    stat integer,
    orig_code character varying(80),
    effective_dt timestamp without time zone NOT NULL,
    eff_dt_est character varying(4),
    ending_dt timestamp without time zone,
    end_dt_est character varying(4),
    distrib_pub1 character varying(80),
    distrib_stat1 character varying(4),
    issue_num1 character varying(20),
    issue_date1 timestamp without time zone,
    distrib_pub2 character varying(80),
    distrib_stat2 character varying(4),
    issue_num2 character varying(20),
    issue_date2 timestamp without time zone,
    sm_archive character varying(4) NOT NULL,
    sm_type_ind character varying(4) NOT NULL,
    sm_airac_ind character varying(4) NOT NULL,
    user_pk double precision,
    publicat_pk double precision,
    source_class double precision DEFAULT 0::double precision,
    rawdataid double precision,
    id1 integer NOT NULL
);


--
-- TOC entry 405 (class 1259 OID 103694)
-- Name: SM_SOURCE_ID1_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE "SM_SOURCE_ID1_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5176 (class 0 OID 0)
-- Dependencies: 405
-- Name: SM_SOURCE_ID1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE "SM_SOURCE_ID1_seq" OWNED BY sm_source.id1;


--
-- TOC entry 203 (class 1259 OID 88547)
-- Name: acftclass; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE acftclass (
    acftclass_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(44),
    codetypeengine character varying(40),
    codeengineno character varying(40),
    acftlandingcat character varying(40),
    anticollsepequip character varying(40),
    classwingspan character varying(20),
    commequip character varying(40),
    navigationequip character varying(40),
    navigationspec character varying(56),
    passengers double precision,
    passengersinterp character varying(44),
    speed double precision,
    speedinterp character varying(44),
    speeduom character varying(40),
    surveillanceequip character varying(40),
    typeaircrafticao character varying(16),
    vertsepcapability character varying(40),
    waketurbulence character varying(40),
    weight double precision,
    weightinterp character varying(44),
    weightuom character varying(40),
    wingspan double precision,
    wingspaninterp character varying(44),
    wingspanuom character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 204 (class 1259 OID 88555)
-- Name: adhpaddress; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE adhpaddress (
    adhpaddress_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(32),
    noseq double precision,
    txtaddress character varying(4000),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 205 (class 1259 OID 88563)
-- Name: adhpcolloc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE adhpcolloc (
    adhpcolloc_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    airport_pk2 double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    codetype character varying(40),
    txtdescr character varying(4000),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 206 (class 1259 OID 88571)
-- Name: adhpgndser; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE adhpgndser (
    adhpgndser_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    codecat character varying(40),
    txtdescrfac character varying(4000),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    txtrmkworkhr_lcl character varying(4000),
    txtdescrfac_lcl character varying(4000),
    txtrmk_lcl character varying(4000),
    codecatreference character varying(40),
    complianticao character varying(40),
    flightoperations character varying(40),
    name character varying(240),
    rank character varying(40),
    snowplan character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 207 (class 1259 OID 88579)
-- Name: adhpgndsvaddr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE adhpgndsvaddr (
    adhpgndsvaddr_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    adhpgndser_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(32),
    noseq double precision,
    txtaddr character varying(4000),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 208 (class 1259 OID 88587)
-- Name: adhpnavaid; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE adhpnavaid (
    adhpnavaid_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    airport_pk double precision,
    navaidfea_pk double precision,
    navaid_pk double precision,
    bltype character varying(8),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 209 (class 1259 OID 88595)
-- Name: adhppsgrfac; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE adhppsgrfac (
    adhppsgrfac_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(32),
    noseq double precision,
    txtdescr character varying(4000),
    txtrmk character varying(4000),
    txtdescr_lcl character varying(4000),
    txtrmk_lcl character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 210 (class 1259 OID 88603)
-- Name: adhpuselimit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE adhpuselimit (
    adhpuselimit_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    noseq double precision,
    usagelimitation character varying(44),
    txtrmk character varying(4000),
    txtrmk_lcl character varying(4000),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    operation character varying(56),
    operationalstatus character varying(40),
    parent_pk double precision,
    parentfea_pk double precision,
    priorpermission double precision,
    priorpermissionuom character varying(40),
    warning character varying(56),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 211 (class 1259 OID 88611)
-- Name: aerogndlgt; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE aerogndlgt (
    aerogndlgt_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtname character varying(240),
    codetype character varying(32),
    txtdescrcharact character varying(4000),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    uomdistver character varying(28),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    txtdescrcharact_lc character varying(4000),
    txtrmkworkhr_lcl character varying(4000),
    txtrmk_lcl character varying(4000),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 212 (class 1259 OID 88619)
-- Name: airport; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airport (
    airport_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    orgauth_pk double precision,
    ident character varying(24),
    nam character varying(240),
    codeicao character varying(16),
    codeiata character varying(12),
    typ character varying(40),
    areacode character varying(12),
    icao character varying(8),
    latitude double precision,
    longitude double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    p_elev double precision,
    p_elevuom character varying(40),
    goidundulation double precision,
    p_geoiduom character varying(40),
    verdatum character varying(40),
    p_vertacc double precision,
    p_vertaccuom character varying(40),
    elevation double precision,
    fieldelevationuom character varying(40),
    elevaccuracy double precision,
    fieldelevaccuom character varying(40),
    descrrefpt character varying(4000),
    descrrefpt_lcl character varying(4000),
    assoccity character varying(240),
    certifiedicao character varying(40),
    privateuse character varying(40),
    controltype character varying(40),
    intlfc character varying(20),
    magtrueind character varying(20),
    magvariation double precision,
    magvaracc double precision,
    magvardate timestamp without time zone,
    magvarchg double precision,
    temperature double precision,
    uomreft character varying(40),
    descracl character varying(4000),
    descracl_lcl character varying(4000),
    altimetercheckloc character varying(40),
    descrsrypwr character varying(4000),
    descrsrypwr_lcl character varying(4000),
    secpowersupply character varying(40),
    descrwdi character varying(4000),
    descrwdi_lcl character varying(4000),
    winddirind character varying(40),
    descrldi character varying(4000),
    descrldi_lcl character varying(4000),
    landingdirind character varying(40),
    transitnalt double precision,
    uomtransalt character varying(40),
    transitnlvl double precision,
    transitionleveluom character varying(40),
    lowesttemperature double precision,
    lowesttempuom character varying(40),
    abandoned character varying(40),
    certificationdate timestamp without time zone,
    certexpdate timestamp without time zone,
    lngrwy double precision,
    uomlngrwy character varying(40),
    lngrwysurf character varying(4),
    speedlimit double precision,
    speedlmtalt character varying(20),
    uomspeedlmtalt character varying(28),
    txtrmk character varying(4000),
    txtrmk_lcl character varying(4000),
    descrsite character varying(4000),
    descrsite_lcl character varying(4000),
    authority character varying(320),
    day_ind character varying(20),
    timezone character varying(4),
    timezonemin double precision,
    codewrkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmkworkhr_lcl character varying(4000),
    airspace_pk double precision,
    airspace_pk2 double precision,
    vor_pk double precision,
    ctldaspind character varying(4),
    ctldaspaptid character varying(24),
    ctldaspapticao character varying(8),
    standmarking character varying,
    standmarking_lcl character varying,
    twyguideline character varying,
    twyguideline_lcl character varying,
    visualparking character varying,
    visualparking_lcl character varying,
    stop_bar_desc character varying,
    stop_bar_dec_lcl character varying,
    s_hacc double precision,
    s_haccuom character varying(40),
    s_elev double precision,
    s_elevuom character varying(40),
    s_geoid double precision,
    s_geoiduom character varying(40),
    s_vertdatum character varying(40),
    s_vertacc double precision,
    s_vertaccuom character varying(40),
    valcrc character varying(32),
    geom geometry(Point,4326),
    geom_s geometry(GeometryCollection,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 213 (class 1259 OID 88627)
-- Name: airporthspot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airporthspot (
    airporthspot_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    s_elevuom character varying(40),
    s_geoid double precision,
    s_geoiduom character varying(40),
    s_vertdatum character varying(40),
    s_vertacc double precision,
    s_vertaccuom character varying(40),
    s_hacc double precision,
    s_haccuom character varying(40),
    ident character varying(64),
    instruction character varying(4000),
    airport_pk double precision,
    s_elev double precision,
    geom geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 214 (class 1259 OID 88635)
-- Name: airportinasp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airportinasp (
    airportinasp_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    airspace_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    bltype character varying(8),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    txtrmk character varying(4000),
    txtrmk_en character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 215 (class 1259 OID 88643)
-- Name: airspace; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airspace (
    airspace_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    typ character varying(44),
    ident character varying(40),
    icaocode character varying(8),
    address character varying(16),
    nam character varying(240),
    areacode character varying(12),
    latitude double precision,
    longitude double precision,
    repunitspeed character varying(4),
    repunitalt character varying(4),
    codedistverup character varying(40),
    upperlimit double precision,
    uirupperlimit double precision,
    uplimitunit character varying(40),
    codedistverlower character varying(40),
    lowerlimit double precision,
    lowerlimitunit character varying(40),
    codedistvermnm character varying(40),
    valdistvermnm double precision,
    uomdistvermnm character varying(40),
    vallowerlimit double precision,
    centerfea_pk double precision,
    unitaddress_pk double precision,
    center_pk double precision,
    txtrmk character varying(4000),
    txtborderrmk character varying(4000),
    cruisetabid character varying(8),
    codewrkhr character varying(20),
    codeclass character varying(20),
    txtrmkwrkhr character varying(4000),
    codelocind character varying(40),
    entryrpt character varying(20),
    levl character varying(20),
    rnp double precision,
    codeactivity character varying(60),
    codemil character varying(40),
    txtlocaltype character varying(240),
    codedistvermax character varying(40),
    valdistvermax double precision,
    uomdistvermax character varying(40),
    airway_pk double precision,
    haccuracy double precision,
    haccuracyuom character varying(40),
    upperlowersepuom character varying(40),
    width double precision,
    widthuom character varying(40),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 216 (class 1259 OID 88651)
-- Name: airspacebdr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airspacebdr (
    airspacebdr_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airspace_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtrmk character varying(4000),
    valwidth double precision,
    uomwidth character varying(40),
    bordertype character varying(4),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 88659)
-- Name: airspaceclass; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airspaceclass (
    airspaceclass_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    classification character varying(40),
    upperlimit double precision,
    upperlimituom character varying(40),
    upperlimitref character varying(40),
    lowerlimit double precision,
    lowerlimituom character varying(40),
    lowerlimitref character varying(40),
    airspace_pk double precision,
    altinterpretation character varying(48),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 88664)
-- Name: airspacevtx; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airspacevtx (
    airspacevtx_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airspacebdr_pk double precision,
    geoborder_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    noseq double precision,
    boundaryvia character varying(20),
    segmentlat double precision,
    segmentlon double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    arcoriglat double precision,
    fixfea_pk double precision,
    fix_pk double precision,
    centerfixfea_pk double precision,
    centerfix_pk double precision,
    arcoriglon double precision,
    arcradius double precision,
    radiusunits character varying(40),
    txtrmk_lcl character varying(4000),
    valcrc character varying(32),
    txtrmk character varying(4000),
    arcbrg double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 219 (class 1259 OID 88672)
-- Name: airspcassoc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airspcassoc (
    airspcassoc_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airspace_pk double precision,
    airspace_pk2 double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    noseqopr double precision,
    codeopr character varying(40),
    dependency character varying(60),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 220 (class 1259 OID 88680)
-- Name: airspcbdrxng; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airspcbdrxng (
    airspcbdrxng_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airspace_pk double precision,
    airspace_pk2 double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    noseq double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 88685)
-- Name: airway; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airway (
    airway_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtdesig character varying(76),
    txtlocdesig character varying(64),
    txtrmk character varying(4000),
    euindicator character varying(20),
    txtrmk_lcl character varying(4000),
    flightrule character varying(40),
    internationaluse character varying(40),
    militaryuse character varying(40),
    miltraningtype character varying(40),
    name character varying(240),
    orgauth_pk double precision,
    type character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    geom geometry(MultiLineString,4326),
    uuid character varying
);


--
-- TOC entry 222 (class 1259 OID 88693)
-- Name: airwayseg; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE airwayseg (
    airwayseg_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airspace_pk double precision,
    airspace_pk2 double precision,
    airspace_pk3 double precision,
    bltype character varying(8),
    airspace_pk4 double precision,
    airspace_pk5 double precision,
    updateoper character varying(60),
    airway_pk double precision,
    source character varying(64),
    txtrmk_lcl character varying(4000),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    codetype character varying(48),
    seq double precision,
    codernp double precision,
    levl character varying(40),
    codeclassacft character varying(20),
    upperlimit double precision,
    uomupperlimit character varying(40),
    codedistverupper character varying(40),
    lowerlimit double precision,
    uomlowerlimit character varying(40),
    codedistverlower character varying(40),
    mnmlimit double precision,
    uommnmlimit character varying(28),
    codedistvermnm character varying(36),
    ovrdelowerlimit double precision,
    uomlwrlmtovrde character varying(28),
    codedstverlwrovrde character varying(36),
    fromfixdesc1 character varying(4),
    fromfixdesc2 character varying(4),
    fromfixdesc3 character varying(20),
    fromfixdesc4 character varying(4),
    tofixdesc1 character varying(4),
    tofixdesc2 character varying(4),
    tofixdesc3 character varying(20),
    tofixdesc4 character varying(4),
    rvsmstart character varying(20),
    rvsmend character varying(20),
    pathtype character varying(40),
    intruetrack double precision,
    inmagtrack double precision,
    revtruetrack double precision,
    revmagtrack double precision,
    routedis double precision,
    changeover double precision,
    uomdist character varying(40),
    segmnt_pk double precision,
    txtrmk character varying(4000),
    areacode character varying(12),
    boundary character varying(4),
    directrest character varying(4),
    cruiseid character varying(8),
    theta double precision,
    codedistverminlev character varying(36),
    rho double precision,
    minlev double precision,
    uomminlev character varying(28),
    vor_pk double precision,
    designatorsuffix character varying(40),
    maxcrossingend double precision,
    maxcrossingendref character varying(40),
    maxcrossingenduom character varying(40),
    mincrossingend double precision,
    mincrossingendref character varying(40),
    mincrossingenduom character varying(40),
    minenroutealt double precision,
    minenroutealtuom character varying(40),
    minobsclearalt double precision,
    minobsclearaltuom character varying(40),
    signalgap character varying(40),
    turndirection character varying(40),
    widthleft double precision,
    widthleftuom character varying(40),
    widthright double precision,
    widthrightuom character varying(40),
    cruisedir character varying(4),
    mnmlimitrev double precision,
    uommnmlimitrev character varying(28),
    codedistvermnmrev character varying(36),
    fixradtransind double precision,
    vor_pk2 double precision,
    totheta double precision,
    torho double precision,
    nonrnavusage character varying(20),
    geom geometry(MultiLineString,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 372 (class 1259 OID 89784)
-- Name: waypoint; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE waypoint (
    waypoint_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airspace_pk double precision,
    airspace_pk2 double precision,
    assocpoint_pk double precision,
    assocpointfea_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    bltype character varying(8),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    ident character varying(20),
    latitude double precision,
    txtrmk_lcl character varying(4000),
    longitude double precision,
    codetype character varying(40),
    typ1 character varying(4),
    typ2 character varying(4),
    typ3 character varying(4),
    usage1 character varying(4),
    usage2 character varying(4),
    areacode character varying(12),
    icaocode character varying(8),
    nam character varying(240),
    magvariation double precision,
    nameind1 character varying(4),
    nameind2 character varying(4),
    magvardate timestamp without time zone,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valcrc character varying(32),
    txtrmk character varying(4000),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 242 (class 1259 OID 88825)
-- Name: dme; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE dme (
    dme_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valchannel double precision,
    codeid character varying(16),
    geolat double precision,
    geolong double precision,
    codetype character varying(40),
    valghostfreq double precision,
    uomghostfreq character varying(40),
    valdisplace double precision,
    uomdisplace character varying(40),
    txtname character varying(240),
    codeem character varying(40),
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    orgauth_pk double precision,
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    areacode character varying(12),
    airspace_pk double precision,
    airspace_pk2 double precision,
    icaocode character varying(8),
    class3 character varying(4),
    class5 character varying(4),
    facchar5 character varying(4),
    codechannel character varying(40),
    valdeclination double precision,
    txtrmk_cover character varying(4000),
    datemagvar timestamp without time zone,
    elevationuom character varying(40),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magneticvariation double precision,
    magvaracc double precision,
    mobile character varying(40),
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 268 (class 1259 OID 89000)
-- Name: marker; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE marker (
    marker_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    codeid character varying(16),
    geolat double precision,
    geolong double precision,
    codeclass character varying(44),
    codepsnils character varying(20),
    valfreq double precision,
    uomfreq character varying(40),
    txtname character varying(240),
    valaxisbrg double precision,
    codeem character varying(40),
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    orgauth_pk double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    areacode character varying(12),
    magvariation double precision,
    magvardate timestamp without time zone,
    icaocode character varying(8),
    icaoseq double precision,
    txtrmk_lcl character varying(4000),
    auralmorsecode character varying(4000),
    elevationuom character varying(40),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magvaracc double precision,
    mobile character varying(40),
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 282 (class 1259 OID 89106)
-- Name: ndb; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ndb (
    ndb_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    codeid character varying(16),
    geolat double precision,
    geolong double precision,
    txtname character varying(240),
    txtrmk_lcl character varying(4000),
    valfreq double precision,
    uomfreq character varying(40),
    codeclass character varying(40),
    codepsnils character varying(20),
    valmagvar double precision,
    datemagvar timestamp without time zone,
    codeem character varying(40),
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    areacode character varying(12),
    icaocode character varying(16),
    airspace_pk double precision,
    airspace_pk2 double precision,
    orgauth_pk double precision,
    class1 character varying(4),
    class3 character varying(4),
    class4 character varying(4),
    class5 character varying(4),
    facchar3 character varying(4),
    facchar4 character varying(4),
    facchar5 double precision,
    valdeclination double precision,
    facchar2 character varying(4),
    txtrmk_cover character varying(4000),
    elevationuom character varying(40),
    emissionband character varying(20),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magvaracc double precision,
    mobile character varying(40),
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 349 (class 1259 OID 89612)
-- Name: tacan; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tacan (
    tacan_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    orgauth_pk double precision,
    airspace_pk double precision,
    airspace_pk2 double precision,
    updateoper character varying(60),
    source character varying(64),
    bltype character varying(8),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    codeid character varying(16),
    geolat double precision,
    geolong double precision,
    txtname character varying(240),
    valchannel double precision,
    codechannel character varying(40),
    valmagvar double precision,
    datemagvar timestamp without time zone,
    codeem character varying(40),
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    areacode character varying(12),
    icaocode character varying(8),
    class2 character varying(4),
    class3 character varying(4),
    class4 character varying(4),
    class5 character varying(4),
    facchar5 character varying(4),
    valdeclination double precision,
    fom character varying(4),
    freqprotdis double precision,
    txtrmk_cover character varying(4000),
    elevationuom character varying(40),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magvaracc double precision,
    mobile character varying(40),
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 370 (class 1259 OID 89768)
-- Name: vor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE vor (
    vor_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airspace_pk double precision,
    airspace_pk2 double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    ident character varying(16),
    latitude double precision,
    longitude double precision,
    areacode character varying(12),
    txtrmk_lcl character varying(4000),
    icaocode character varying(8),
    txtname character varying(240),
    magvariation double precision,
    magvardate timestamp without time zone,
    elevation double precision,
    elevaccuracy double precision,
    frequency double precision,
    class3 character varying(4),
    class4 character varying(4),
    class5 character varying(4),
    facchar1 character varying(4),
    facchar2 character varying(4),
    freqprotdis double precision,
    frequnits character varying(40),
    fom character varying(4),
    vortype character varying(40),
    codeem character varying(40),
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    geoundulation double precision,
    valcrc character varying(32),
    verdatum character varying(40),
    codewrkhr character varying(20),
    txtrmkwrkhr character varying(4000),
    txtrmk character varying(4000),
    orgauth_pk double precision,
    antennaheight double precision,
    power double precision,
    ndb_pk double precision,
    valdeclination double precision,
    magtrueind character varying(40),
    txtrmk_cover character varying(4000),
    elevationuom character varying(40),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magvaracc double precision,
    mobile character varying(40),
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 223 (class 1259 OID 88701)
-- Name: altrecdist; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE altrecdist (
    altrecdist_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    altrecord_pk double precision,
    alternatefea_pk double precision,
    alternate_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    bltype character varying(8),
    mslink double precision,
    mapid double precision,
    seq double precision,
    disttoalt double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 88706)
-- Name: altrecord; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE altrecord (
    altrecord_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    altrelatedfea_pk double precision,
    altrelated_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    bltype character varying(8),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    alttype character varying(8),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 88711)
-- Name: apron; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE apron (
    apron_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtname character varying(240),
    codecomposition character varying(52),
    codecondsfc character varying(40),
    txtrmk_lcl character varying(4000),
    codests character varying(64),
    txtmarking character varying(4000),
    txtrmk character varying(4000),
    preparation character varying(44),
    pcnclass double precision,
    pcnpavementtype character varying(40),
    pcnpavementsubgrad character varying(40),
    codepcnmaxtirepres character varying(40),
    valpcnmaxtirepres double precision,
    pcnevalmethod character varying(40),
    pcnnote character varying(4000),
    lcnclass double precision,
    siwlweight double precision,
    uomsiwlweight character varying(40),
    siwltirepress double precision,
    uomsiwltirepress character varying(40),
    auwweight double precision,
    uomauwweight character varying(40),
    abandoned character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 88719)
-- Name: apronelem_geom; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE apronelem_geom (
    apronelem_geom_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    radiusuom character varying(40),
    radius double precision,
    origlon double precision,
    origlat double precision,
    apronelement_pk double precision,
    longitude double precision,
    latitude double precision,
    seq double precision,
    path character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 227 (class 1259 OID 88724)
-- Name: apronelement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE apronelement (
    apronelement_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    verticaldatum character varying(40),
    geoidundulationuom character varying(40),
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    verticalaccuracy double precision,
    geoidundulation double precision,
    elevationuom character varying(40),
    elevation double precision,
    txtrmk character varying(4000),
    weightauwuom character varying(40),
    weightauw double precision,
    tyrepresssiwluom character varying(40),
    tyrepressuresiwl double precision,
    apron_pk double precision,
    type character varying(40),
    noseq double precision,
    jetwayavailability character varying(40),
    towingavailability character varying(40),
    dockingavail character varying(40),
    groundpoweravail character varying(40),
    length double precision,
    lengthuom character varying(40),
    width double precision,
    widthuom character varying(40),
    composition character varying(52),
    preparation character varying(44),
    surfacecondition character varying(40),
    classpcn double precision,
    pavementtypepcn character varying(40),
    pavesubgradepcn character varying(40),
    maxtyrepressurepcn character varying(40),
    evalmethodpcn character varying(40),
    classlcn double precision,
    weightsiwl double precision,
    weightsiwluom character varying(40),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 88732)
-- Name: apronlgtsys; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE apronlgtsys (
    apronlgtsys_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    apron_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codepsn character varying(20),
    txtdescr character varying(4000),
    txtdescremerg character varying(4000),
    codeinst character varying(20),
    codecolour character varying(20),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 88740)
-- Name: arrestinggear; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE arrestinggear (
    arrestinggear_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    ident character varying(240),
    status character varying(64),
    length double precision,
    lengthuom character varying(40),
    width double precision,
    widthuom character varying(40),
    engagedevice character varying(80),
    absorbtype character varying(108),
    bidirectional character varying(40),
    location double precision,
    locationuom character varying(40),
    auwweight double precision,
    codecomposition character varying(52),
    codecondsfc character varying(40),
    codepcnmaxtirepres character varying(40),
    lcnclass double precision,
    pcnclass double precision,
    pcnevalmethod character varying(40),
    pcnpavementsubgrad character varying(40),
    pcnpavementtype character varying(40),
    preparation character varying(44),
    siwltirepress double precision,
    siwlweight double precision,
    uomauwweight character varying(40),
    uomsiwltirepress character varying(40),
    uomsiwlweight character varying(40),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    latitude double precision,
    longitude double precision,
    geom_type character varying(40),
    geom geometry(GeometryCollection),
    geom_c geometry(GeometryCollection),
    geom_s geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 230 (class 1259 OID 88748)
-- Name: arrstnggrrwydir; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE arrstnggrrwydir (
    arrstnggrrwydir_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    arrestinggear_pk double precision,
    rwydirection_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 88753)
-- Name: authforasp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE authforasp (
    authforasp_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    orgauth_pk double precision,
    airspace_pk double precision,
    codetype character varying(24),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 232 (class 1259 OID 88761)
-- Name: awyrst; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE awyrst (
    awyrst_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    fromfixfea_pk double precision,
    fromfix_pk double precision,
    tofixfea_pk double precision,
    tofix_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    bltype character varying(8),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    routeident character varying(52),
    restrident character varying(12),
    typ character varying(8),
    startdate character varying(28),
    enddate character varying(28),
    timecode character varying(4),
    timeind character varying(4),
    exclusionind character varying(4),
    altunits character varying(4),
    note character varying(960),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 233 (class 1259 OID 88769)
-- Name: awyrstalt; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE awyrstalt (
    awyrstalt_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    awyrst_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    seq double precision,
    restralt double precision,
    blockind character varying(4),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 234 (class 1259 OID 88774)
-- Name: awyrstlnk; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE awyrstlnk (
    awyrstlnk_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    awyrst_pk double precision,
    airwayseg_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 235 (class 1259 OID 88779)
-- Name: awyrstto; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE awyrstto (
    awyrstto_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    awyrst_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    seq double precision,
    timeofoperatn character varying(40),
    cruisetabid character varying(8),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 432 (class 1259 OID 112118)
-- Name: boundingbox; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE boundingbox (
    id integer NOT NULL,
    name character varying,
    description text,
    geom geometry
);


--
-- TOC entry 431 (class 1259 OID 112116)
-- Name: boundingbox_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE boundingbox_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5177 (class 0 OID 0)
-- Dependencies: 431
-- Name: boundingbox_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE boundingbox_id_seq OWNED BY boundingbox.id;


--
-- TOC entry 236 (class 1259 OID 88784)
-- Name: callsign; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE callsign (
    callsign_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtcallsign character varying(240),
    codelang character varying(12),
    txtrmk character varying(4000),
    service_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 237 (class 1259 OID 88792)
-- Name: conditioncombo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE conditioncombo (
    conditioncombo_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    logicaloperator character varying(40),
    acftclass_pk double precision,
    adhpuselimit_pk double precision,
    meteorology_pk double precision,
    conditioncombo_pk2 double precision,
    flightclass_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 238 (class 1259 OID 88799)
-- Name: coroute; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE coroute (
    coroute_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    airport_pk double precision,
    airport_pk2 double precision,
    bltype character varying(8),
    tofixfea_pk double precision,
    tofix_pk double precision,
    airport_pk3 double precision,
    mslink double precision,
    mapid double precision,
    customercode character varying(12),
    routeident character varying(40),
    seq double precision,
    routevia character varying(12),
    ssaawy character varying(24),
    areacode character varying(12),
    rwytrans character varying(20),
    enrtetrans character varying(20),
    cruisealt character varying(20),
    altdist character varying(16),
    costindex character varying(12),
    enroutealtapt character varying(16),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 239 (class 1259 OID 88804)
-- Name: deicingarea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE deicingarea (
    deicingarea_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    twy_pk double precision,
    uomsiwltirepress character varying(40),
    uomauwweight character varying(40),
    siwlweight double precision,
    siwltirepress double precision,
    preparation character varying(44),
    pcnpavementtype character varying(40),
    pcnpavementsubgrad character varying(40),
    pcnevalmethod character varying(40),
    pcnclass double precision,
    lcnclass double precision,
    codepcnmaxtirepres character varying(40),
    codecondsfc character varying(40),
    codecomposition character varying(52),
    auwweight double precision,
    s_haccuom character varying(40),
    s_hacc double precision,
    s_vertaccuom character varying(40),
    s_vertacc double precision,
    s_vertdatum character varying(40),
    s_geoiduom character varying(40),
    s_geoid double precision,
    ident character varying(40),
    apron_pk double precision,
    uomsiwlweight character varying(40),
    gatestand_pk double precision,
    s_elev double precision,
    s_elevuom character varying(40),
    geom geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 240 (class 1259 OID 88812)
-- Name: directflt; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE directflt (
    directflt_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valexceedlen double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 241 (class 1259 OID 88817)
-- Name: dirfinder; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE dirfinder (
    dirfinder_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    orgauth_pk double precision,
    doppler character varying(40),
    txtrmk character varying(4000),
    longitude double precision,
    latitude double precision,
    haccuracyuom character varying(40),
    haccuracy double precision,
    vertaccuom character varying(40),
    verticalaccuracy double precision,
    verticaldatum character varying(40),
    geoidundulationuom character varying(40),
    geoidundulation double precision,
    elevationuom character varying(40),
    elevation double precision,
    flightchecked character varying(40),
    datemagvar timestamp without time zone,
    magvaracc double precision,
    magneticvariation double precision,
    mobile character varying(40),
    emissionclass character varying(40),
    name character varying(240),
    designator character varying(16),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 243 (class 1259 OID 88833)
-- Name: dmelimitation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE dmelimitation (
    dmelimitation_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    dme_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    valanglefm double precision,
    valangleto double precision,
    valdistinner double precision,
    valdistverlower double precision,
    valdistouter double precision,
    valdistverupper double precision,
    uomdistverupper character varying(40),
    codedistverupper character varying(40),
    uomdistverlower character varying(40),
    codedistverlower character varying(40),
    valanglescallop double precision,
    txtrmk character varying(4000),
    angledirref character varying(40),
    angletype character varying(40),
    arcdirection character varying(40),
    innerdistanceuom character varying(40),
    outerdistanceuom character varying(40),
    signaltype character varying(40),
    geom geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 244 (class 1259 OID 88841)
-- Name: flightclass; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE flightclass (
    flightclass_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    coderule character varying(40),
    codestatus character varying(40),
    military character varying(40),
    origin character varying(40),
    purpose character varying(52),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 245 (class 1259 OID 88846)
-- Name: flowcndellvl; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE flowcndellvl (
    flowcndellvl_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    flowcondelem_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valdistverlower double precision,
    uomdistverlower character varying(28),
    codedistverlower character varying(36),
    valdistverupper double precision,
    uomdistverupper character varying(28),
    codedistverupper character varying(36),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 246 (class 1259 OID 88851)
-- Name: flowcondcomb; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE flowcondcomb (
    flowcondcomb_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    parentfea_pk double precision,
    parent_pk double precision,
    flowcondcomb_pk2 double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    bltype character varying(8),
    mslink double precision,
    mapid double precision,
    noseq double precision,
    codeopr character varying(24),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 247 (class 1259 OID 88856)
-- Name: flowcondelem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE flowcondelem (
    flowcondelem_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    flowcondcomb_pk double precision,
    parentfea_pk double precision,
    parent_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    bltype character varying(8),
    mslink double precision,
    mapid double precision,
    noseq double precision,
    coderefloc character varying(20),
    coderelwthloc character varying(12),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 248 (class 1259 OID 88861)
-- Name: fltplnardpfix; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE fltplnardpfix (
    fltplnardpfix_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    fltplnarrdep_pk double precision,
    intfixfea_pk double precision,
    intfix_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    bltype character varying(8),
    mslink double precision,
    mapid double precision,
    intdistance double precision,
    trnscode character varying(4),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 249 (class 1259 OID 88866)
-- Name: fltplnardptim; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE fltplnardptim (
    fltplnardptim_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    fltplnarrdep_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    seq double precision,
    timecode character varying(4),
    timeind character varying(4),
    timeofoper character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 250 (class 1259 OID 88871)
-- Name: fltplnarrdep; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE fltplnarrdep (
    fltplnarrdep_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    ssa_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    rwytrnsfixfea_pk double precision,
    rwytrnsfix_pk double precision,
    bltype character varying(8),
    mslink double precision,
    cmntrnsfixfea_pk double precision,
    cmntrnsfix_pk double precision,
    mapid double precision,
    enrtrnsfixfea_pk double precision,
    enrtrnsfix_pk double precision,
    proceduretype character varying(4),
    seq double precision,
    rwytrnsident character varying(20),
    rwytrnstrckdis double precision,
    cmnsegtrckdis double precision,
    enrtrnsident character varying(20),
    enrtrnstrckdis double precision,
    numengrst character varying(16),
    tboprpjetind character varying(4),
    rnavflag character varying(20),
    atcweightcat character varying(4),
    atcident character varying(28),
    timecode character varying(4),
    procdescript character varying(60),
    legtypcode character varying(8),
    reportingcode character varying(4),
    initdepmagcrs character varying(16),
    altdescript character varying(4),
    altitude1 character varying(20),
    altitude2 character varying(20),
    speedlimit double precision,
    initcruisetab character varying(8),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 251 (class 1259 OID 88876)
-- Name: frequency; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE frequency (
    frequency_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    service_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valfreqtrans double precision,
    valfreqrec double precision,
    codetype character varying(20),
    codeem character varying(40),
    codeselcal character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    modulation character varying(4),
    aptservind1 character varying(4),
    aptservind2 character varying(4),
    aptservind3 character varying(4),
    enrservind1 character varying(4),
    enrservind2 character varying(4),
    enrservind3 character varying(4),
    altdesc character varying(4),
    altitude1 double precision,
    altitude2 double precision,
    uomalt1 character varying(28),
    uomalt2 character varying(28),
    codealtitude1 character varying(36),
    codealtitude2 character varying(36),
    sectorstart double precision,
    sectorend double precision,
    sectorfacfea_pk double precision,
    sectorfac_pk double precision,
    distancedesc character varying(4),
    distance double precision,
    remotefacfea_pk double precision,
    remotefac_pk double precision,
    timeind character varying(4),
    channel character varying(40),
    flightchecked character varying(40),
    freqrecuom character varying(40),
    freqtransuom character varying(40),
    mmode character varying(40),
    rank character varying(40),
    trafficdirection character varying(52),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 252 (class 1259 OID 88884)
-- Name: fuel; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE fuel (
    fuel_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codecat character varying(40),
    txtdescr character varying(4000),
    txtrmk character varying(4000),
    txtdescr_lcl character varying(4000),
    adhpgndser_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 253 (class 1259 OID 88892)
-- Name: gatestand; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE gatestand (
    gatestand_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtdesig character varying(64),
    codetype character varying(40),
    txtdscrrestruse character varying(4000),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    txtrmk character varying(4000),
    apronelement_pk double precision,
    classlcn double precision,
    classpcn double precision,
    composition character varying(52),
    elevationuom character varying(40),
    evalmethodpcn character varying(40),
    geoidundulationuom character varying(40),
    maxtyrepressurepcn character varying(40),
    pavementtypepcn character varying(40),
    pavesubgradepcn character varying(40),
    preparation character varying(44),
    surfacecondition character varying(40),
    tyrepresssiwluom character varying(40),
    tyrepressuresiwl double precision,
    vertaccuom character varying(40),
    visualdockingsys character varying(44),
    weightauw double precision,
    weightauwuom character varying(40),
    weightsiwl double precision,
    weightsiwluom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 254 (class 1259 OID 88900)
-- Name: geoborder; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE geoborder (
    geoborder_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtname character varying(240),
    codetype character varying(40),
    txtrmk character varying(4000),
    haccuracy double precision,
    haccuracyuom character varying(40),
    geom geometry(MultiLineString,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 255 (class 1259 OID 88908)
-- Name: geobordervtx; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE geobordervtx (
    geobordervtx_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    geoborder_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    noseq double precision,
    boundaryvia character varying(20),
    segmentlat double precision,
    segmentlon double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valcrc character varying(32),
    txtrmk character varying(4000),
    arcoriglat double precision,
    arcoriglon double precision,
    arcradius double precision,
    radiusunits character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 256 (class 1259 OID 88916)
-- Name: georeflink; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE georeflink (
    georeflink_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    georeftable_pk double precision,
    updateoper character varying(60),
    prroute_pk double precision,
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    seq double precision,
    prrouteuseind character varying(8),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 257 (class 1259 OID 88921)
-- Name: georeftable; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE georeftable (
    georeftable_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    areacode character varying(12),
    georeftable character varying(8),
    geoentity character varying(116),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 258 (class 1259 OID 88926)
-- Name: gls; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE gls (
    gls_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    parentfea_pk double precision,
    parent_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    glsrefpathident character varying(16),
    glscategory character varying(4),
    glschannel double precision,
    glsappbrg character varying(16),
    stationlat double precision,
    stationlon double precision,
    glsstatnident character varying(16),
    svcvolradius double precision,
    tdmaslots character varying(8),
    glsappslope double precision,
    magvariation double precision,
    statnelevation double precision,
    datumcode character varying(12),
    stationtype character varying(12),
    wgs84stnelevation double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 259 (class 1259 OID 88931)
-- Name: gplimitation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE gplimitation (
    gplimitation_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    ilsgp_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    valanglefm double precision,
    valangleto double precision,
    valdistinner double precision,
    valdistverlower double precision,
    valdistouter double precision,
    valdistverupper double precision,
    uomdistverupper character varying(40),
    codedistverupper character varying(40),
    uomdistverlower character varying(40),
    codedistverlower character varying(40),
    valanglescallop double precision,
    txtrmk character varying(4000),
    angledirref character varying(40),
    angletype character varying(40),
    arcdirection character varying(40),
    innerdistanceuom character varying(40),
    outerdistanceuom character varying(40),
    signaltype character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 260 (class 1259 OID 88939)
-- Name: gridmora; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE gridmora (
    gridmora_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    startlat double precision,
    startlon double precision,
    gridsize double precision,
    mora1 character varying(12),
    mora2 character varying(12),
    mora3 character varying(12),
    mora4 character varying(12),
    mora5 character varying(12),
    mora6 character varying(12),
    mora7 character varying(12),
    mora8 character varying(12),
    mora9 character varying(12),
    mora10 character varying(12),
    mora11 character varying(12),
    mora12 character varying(12),
    mora13 character varying(12),
    mora14 character varying(12),
    mora15 character varying(12),
    mora16 character varying(12),
    mora17 character varying(12),
    mora18 character varying(12),
    mora19 character varying(12),
    mora20 character varying(12),
    mora21 character varying(12),
    mora22 character varying(12),
    mora23 character varying(12),
    mora24 character varying(12),
    mora25 character varying(12),
    mora26 character varying(12),
    mora27 character varying(12),
    mora28 character varying(12),
    mora29 character varying(12),
    mora30 character varying(12),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 261 (class 1259 OID 88947)
-- Name: guidanceline; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE guidanceline (
    guidanceline_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    twy_pk double precision,
    apron_pk double precision,
    rwyclinept_pk double precision,
    tlof_pk double precision,
    designator character varying(240),
    type character varying(40),
    maxspeed double precision,
    maxspeeduom character varying(40),
    usagedirection character varying(40),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    txtrmk character varying(4000),
    gatestand_pk double precision,
    geom geometry(MultiLineString,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 374 (class 1259 OID 103534)
-- Name: has_annotation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE has_annotation (
    has_annotation_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mapid double precision,
    mslink double precision,
    bltype character varying(8),
    note_pk double precision,
    fea_pk integer,
    attr_pk double precision,
    attr_dbname character varying(120),
    occ_pk double precision,
    purpose character varying(240),
    seq double precision
);


--
-- TOC entry 262 (class 1259 OID 88955)
-- Name: holdingproc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE holdingproc (
    holdingproc_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    fixfea_pk double precision,
    fix_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    codetype character varying(20),
    txtdescr character varying(4000),
    txtrmk character varying(4000),
    rnp double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 263 (class 1259 OID 88963)
-- Name: ilsgp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ilsgp (
    ilsgp_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valfreq double precision,
    uomfreq character varying(40),
    codeem character varying(40),
    valslope double precision,
    valrdh double precision,
    uomrdh character varying(40),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    gsposition double precision,
    gsbeamwidth double precision,
    txtrmk_cover character varying(4000),
    angleaccuracy double precision,
    datemagvar timestamp without time zone,
    designator character varying(16),
    elevationuom character varying(40),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magneticvariation double precision,
    magvaracc double precision,
    mobile character varying(40),
    name character varying(240),
    orgauth_pk double precision,
    rdhaccuracy double precision,
    rdhaccuracyuom character varying(40),
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 264 (class 1259 OID 88971)
-- Name: ilsllz; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ilsllz (
    ilsllz_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codeid character varying(16),
    valfreq double precision,
    uomfreq character varying(40),
    codeem character varying(40),
    valmagbrg double precision,
    valtruebrg double precision,
    valwidcourse double precision,
    codetypeuseback character varying(40),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    locposition double precision,
    locazposref character varying(4),
    facchar1 character varying(4),
    facchar4 character varying(4),
    govtsource character varying(20),
    valdeclination double precision,
    valmagvar double precision,
    datemagvar timestamp without time zone,
    txtrmk_cover character varying(4000),
    elevationuom character varying(40),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magbearingacc double precision,
    magvaracc double precision,
    mobile character varying(40),
    name character varying(240),
    orgauth_pk double precision,
    truebearingacc double precision,
    vertaccuom character varying(40),
    widthcourceacc double precision,
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 265 (class 1259 OID 88979)
-- Name: lgtelement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE lgtelement (
    lgtelement_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    colour character varying(40),
    intensitylevel character varying(52),
    obstaclepart_pk double precision,
    haccuracyuom character varying(40),
    intensity double precision,
    intensityuom character varying(20),
    type character varying(40),
    latitude double precision,
    longitude double precision,
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    geom geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 266 (class 1259 OID 88987)
-- Name: lgtelemstatus; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE lgtelemstatus (
    lgtelemstatus_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    status character varying(64),
    ident character varying(40),
    lgtelement_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 267 (class 1259 OID 88992)
-- Name: llzlimitation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE llzlimitation (
    llzlimitation_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    ilsllz_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    valanglefm double precision,
    valangleto double precision,
    valdistinner double precision,
    valdistverlower double precision,
    valdistouter double precision,
    valdistverupper double precision,
    uomdistverupper character varying(40),
    codedistverupper character varying(40),
    uomdistverlower character varying(40),
    codedistverlower character varying(40),
    valanglescallop double precision,
    txtrmk character varying(4000),
    angledirref character varying(40),
    angletype character varying(40),
    arcdirection character varying(40),
    innerdistanceuom character varying(40),
    outerdistanceuom character varying(40),
    signaltype character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 269 (class 1259 OID 89008)
-- Name: marking; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE marking (
    marking_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    marked_pk double precision,
    markedfea_pk double precision,
    markinglocation character varying(44),
    condition character varying(40),
    markingicaostd character varying(40),
    ident character varying(240),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 270 (class 1259 OID 89016)
-- Name: markingelement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE markingelement (
    markingelement_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    style character varying(40),
    geom_type character varying(40),
    longitude double precision,
    latitude double precision,
    haccuracyuom character varying(40),
    ident character varying(240),
    colour character varying(40),
    marking_pk double precision,
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    geom geometry(GeometryCollection),
    geom_c geometry(GeometryCollection),
    geom_s geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 271 (class 1259 OID 89024)
-- Name: meteorology; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE meteorology (
    meteorology_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    flightconditions character varying(40),
    visibility double precision,
    visibilityuom character varying(40),
    visibilityinterp character varying(44),
    rwyvisualrange double precision,
    rwyvisualrangeuom character varying(40),
    rwyvisrangeinterp character varying(44),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 272 (class 1259 OID 89029)
-- Name: metsvc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE metsvc (
    metsvc_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    service_pk double precision,
    unit_pk double precision,
    unit_pk2 double precision,
    unit_pk3 double precision,
    codevalidity character varying(20),
    codelndgforecast character varying(8),
    codebriefing character varying(4),
    txtlang character varying(160),
    txtcharts character varying(4000),
    txtequipment character varying(4000),
    txtrmk character varying(4000),
    txtcharts_lcl character varying(4000),
    txtequip_lcl character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 273 (class 1259 OID 89037)
-- Name: mlsazimuth; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE mlsazimuth (
    mlsazimuth_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    valtruebrg double precision,
    valmagbrg double precision,
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    valanglepropleft double precision,
    valanglepropright double precision,
    valanglecoverleft double precision,
    valanglecoverright double precision,
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    magvariation double precision,
    azimuthpos double precision,
    azimuthposref character varying(4),
    magvardate timestamp without time zone,
    channel character varying(40),
    designator character varying(16),
    elevationuom character varying(40),
    emissionclass character varying(40),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magvaracc double precision,
    mobile character varying(40),
    name character varying(240),
    orgauth_pk double precision,
    truebearingacc double precision,
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 274 (class 1259 OID 89045)
-- Name: mlselevation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE mlselevation (
    mlselevation_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valanglenml double precision,
    valanglemnm double precision,
    valanglespan double precision,
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    angleaccuracy double precision,
    datemagvar timestamp without time zone,
    designator character varying(16),
    elevationuom character varying(40),
    emissionclass character varying(40),
    flightchecked character varying(40),
    geoidundulationuom character varying(40),
    magneticvariation double precision,
    magvaracc double precision,
    mobile character varying(40),
    name character varying(240),
    orgauth_pk double precision,
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 275 (class 1259 OID 89053)
-- Name: msa; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE msa (
    msa_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    msagroup_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    anglefm double precision,
    angleto double precision,
    distouter double precision,
    distinner double precision,
    uomdistver character varying(40),
    codedistver character varying(40),
    distver double precision,
    txtrmk character varying(4000),
    angledirrf character varying(40),
    angletype character varying(40),
    arcdirection character varying(40),
    bufferwidth double precision,
    bufferwidthuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    innerdistanceuom character varying(40),
    outerdistanceuom character varying(40),
    upperlimit double precision,
    upperlimitref character varying(40),
    upperlimituom character varying(40),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 276 (class 1259 OID 89061)
-- Name: msagroup; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE msagroup (
    msagroup_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    navaidfea_pk double precision,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    navaid_pk double precision,
    diffind character varying(4),
    bltype character varying(8),
    mslink double precision,
    mapid double precision,
    typeangle character varying(20),
    txtrmk character varying(4000),
    coderefangle character varying(16),
    safeareatype character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 277 (class 1259 OID 89069)
-- Name: navaids; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE navaids (
    navaids_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    type character varying(40),
    designator character varying(16),
    longitude double precision,
    latitude double precision,
    tlof_pk double precision,
    rwydirection_pk double precision,
    name character varying(240),
    flightchecked character varying(40),
    purpose character varying(40),
    signalperformance character varying(40),
    coursequality character varying(40),
    integritylevel character varying(40),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    txtrmk character varying(4000),
    airport_pk double precision,
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 278 (class 1259 OID 89077)
-- Name: navaidscomp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE navaidscomp (
    navaidscomp_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    provnavloc character varying(40),
    markerposition character varying(40),
    collocationgroup double precision,
    equipfea_pk double precision,
    navaids_pk double precision,
    equip_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 279 (class 1259 OID 89082)
-- Name: navsysangind; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE navsysangind (
    navsysangind_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    pointfea_pk double precision,
    point_pk double precision,
    transmitfea_pk double precision,
    transmit_pk double precision,
    bltype character varying(8),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    valanglebrg double precision,
    txtrmk character varying(4000),
    valcrc character varying(32),
    angletype character varying(40),
    cardinaldirection character varying(40),
    indicationdir character varying(40),
    minrecalt double precision,
    minrecaltuom character varying(40),
    trueangle double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 280 (class 1259 OID 89090)
-- Name: navsyschkpt; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE navsyschkpt (
    navsyschkpt_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    parentfea_pk double precision,
    parent_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    codetype character varying(24),
    txtrmk character varying(4000),
    txtrmk_lcl character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 281 (class 1259 OID 89098)
-- Name: navsysdistind; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE navsysdistind (
    navsysdistind_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    pointfea_pk double precision,
    point_pk double precision,
    transmfea_pk double precision,
    bltype character varying(8),
    transm_pk double precision,
    mslink double precision,
    mapid double precision,
    uomdist character varying(40),
    valdist double precision,
    txtrmk character varying(4000),
    valcrc character varying(32),
    minrecalt double precision,
    minrecaltuom character varying(40),
    type character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 283 (class 1259 OID 89114)
-- Name: ndblimitation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ndblimitation (
    ndblimitation_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    ndb_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    valanglefm double precision,
    valangleto double precision,
    valdistinnner double precision,
    valdistverlower double precision,
    valdistouter double precision,
    valdistverupper double precision,
    uomdistverupper character varying(40),
    codedistverupper character varying(40),
    uomdistverlower character varying(40),
    codedistverlower character varying(40),
    valanglescallop double precision,
    txtrmk character varying(4000),
    angledirref character varying(40),
    angletype character varying(40),
    arcdirection character varying(40),
    innerdistanceuom character varying(40),
    outerdistanceuom character varying(40),
    signaltype character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 284 (class 1259 OID 89122)
-- Name: nitrogen; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE nitrogen (
    nitrogen_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    txtrmk character varying(4000),
    adhpgndser_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 285 (class 1259 OID 89130)
-- Name: ntim_template; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ntim_template (
    ntim_template_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    templ_desc character varying(960),
    latitude double precision,
    longitude double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 286 (class 1259 OID 89138)
-- Name: nvdtimesheet; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE nvdtimesheet (
    nvdtimesheet_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    parentfea_pk double precision,
    parent_pk double precision,
    codetype character varying(16),
    monthvalidwef character varying(20),
    dayvalidwef double precision,
    codeday character varying(48),
    hrvalidwef double precision,
    minvalidwef double precision,
    codeeventwef character varying(20),
    codetimeref character varying(40),
    monthvalidtil character varying(20),
    dayvalidtil double precision,
    codedaytil character varying(48),
    timereleventwef double precision,
    codecombwef character varying(32),
    hourtil double precision,
    minutetil double precision,
    codeeventtil character varying(20),
    timereleventtil double precision,
    codecombtil character varying(32),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 287 (class 1259 OID 89143)
-- Name: obsarea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE obsarea (
    obsarea_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    haccuracy double precision,
    orgauth_pk double precision,
    type character varying(40),
    obsidsurfacecond character varying(60),
    rwydirection_pk double precision,
    airport_pk double precision,
    haccuracyuom character varying(40),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 288 (class 1259 OID 89151)
-- Name: obsarealink; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE obsarealink (
    obsarealink_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    obstacle_pk double precision,
    uomdisthoriz character varying(40),
    typeops character varying(20),
    txtrmk character varying(4000),
    brgthr double precision,
    disttocline double precision,
    distthr double precision,
    distalongcline double precision,
    obsarea_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 289 (class 1259 OID 89159)
-- Name: obsassessarea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE obsassessarea (
    obsassessarea_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    parent_pk double precision,
    parentfea_pk double precision,
    c_haccuracyuom character varying(40),
    c_haccuracy double precision,
    s_haccuracyuom character varying(40),
    s_haccuracy double precision,
    safetyregulation character varying(240),
    gradientlowhigh double precision,
    slopeloweraltuom character varying(40),
    slopeloweralt double precision,
    assessedaltuom character varying(40),
    assessedalt double precision,
    slope double precision,
    sectionnumber double precision,
    type character varying(60),
    surfacezone character varying(40),
    geom geometry(MultiPolygon,4326),
    geom_c geometry(GeometryCollection,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 290 (class 1259 OID 89167)
-- Name: obspart_geom; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE obspart_geom (
    obspart_geom_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    radius double precision,
    origlon double precision,
    origlat double precision,
    path character varying(40),
    radiusuom character varying(40),
    latitude double precision,
    seq double precision,
    obstaclepart_pk double precision,
    longitude double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 291 (class 1259 OID 89175)
-- Name: obstacle; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE obstacle (
    obstacle_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    typ character varying(100),
    latitude double precision,
    longitude double precision,
    lighted character varying(40),
    descrlgt character varying(4000),
    descrmrk character varying(4000),
    multioccur character varying(40),
    descript character varying(4000),
    location character varying(240),
    codedatum character varying(12),
    valcrc character varying(32),
    txtrmk character varying(4000),
    ident character varying(64),
    length double precision,
    lengthuom character varying(40),
    lightingicaostd character varying(40),
    markingicaostd character varying(40),
    radius double precision,
    radiusuom character varying(40),
    synclighting character varying(40),
    width double precision,
    widthuom character varying(40),
    geom geometry(GeometryCollection,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 292 (class 1259 OID 89183)
-- Name: obstacleinssa; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE obstacleinssa (
    obstacleinssa_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    obstacle_pk double precision,
    ssa_pk double precision,
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    remark character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 293 (class 1259 OID 89191)
-- Name: obstaclepart; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE obstaclepart (
    obstaclepart_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    obstacle_pk double precision,
    verticalextent double precision,
    verticalextentuom character varying(40),
    vertextentacc double precision,
    vertextentaccuom character varying(40),
    noseq double precision,
    type character varying(100),
    constructionstatus character varying(72),
    markingpattern character varying(40),
    markingfirstcolour character varying(40),
    markingsecondcol character varying(40),
    mobile character varying(40),
    frangible character varying(40),
    visiblematerial character varying(80),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    txtrmk character varying(4000),
    geom_type character varying(40),
    geom geometry(Point,4326),
    geom_c geometry(MultiLineString,4326),
    geom_s geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 294 (class 1259 OID 89199)
-- Name: ocaoch; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ocaoch (
    ocaoch_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    ocaochapptyp_pk double precision,
    codecatacft character varying(24),
    valoca double precision,
    codeoca character varying(40),
    coderefoca character varying(40),
    valoch double precision,
    codeoch character varying(40),
    coderefoch character varying(20),
    uomdistver character varying(28),
    txtrmk character varying(4000),
    miltaryheight double precision,
    miltaryheightuom character varying(40),
    radioheight double precision,
    radioheightuom character varying(40),
    visibility double precision,
    visibilityuom character varying(40),
    milvisibility double precision,
    milvisibilityuom character varying(40),
    mandatoryrvr character varying(20),
    remotealtminima character varying(20),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 295 (class 1259 OID 89207)
-- Name: ocaochapptyp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ocaochapptyp (
    ocaochapptyp_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    ssa_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetypeapch character varying(48),
    climbgradient double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 296 (class 1259 OID 89212)
-- Name: oil; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE oil (
    oil_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codecat character varying(40),
    txtdescr character varying(4000),
    txtrmk character varying(4000),
    txtdescr_lcl character varying(4000),
    adhpgndser_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 297 (class 1259 OID 89220)
-- Name: orgauth; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE orgauth (
    orgauth_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtname character varying(240),
    codeid character varying(48),
    codetype character varying(60),
    txtrmk character varying(4000),
    military character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 298 (class 1259 OID 89228)
-- Name: orgauthaddr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE orgauthaddr (
    orgauthaddr_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    orgauth_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(32),
    noseq double precision,
    txtaddress character varying(4000),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 299 (class 1259 OID 89236)
-- Name: orgauthassoc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE orgauthassoc (
    orgauthassoc_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    orgauth_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    orgauth_pk2 double precision,
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    codetype character varying(52),
    txtrmk character varying(4000),
    noseq double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 300 (class 1259 OID 89244)
-- Name: oxygen; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE oxygen (
    oxygen_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    txtrmk character varying(4000),
    adhpgndser_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 301 (class 1259 OID 89252)
-- Name: pdflvl; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE pdflvl (
    pdflvl_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    pdflvlcol_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valdistver double precision,
    vertdistanceuom character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 302 (class 1259 OID 89257)
-- Name: pdflvlcol; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE pdflvlcol (
    pdflvlcol_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    pdflvltbl_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codeid character varying(4),
    txtrmk character varying(4000),
    separation character varying(40),
    series character varying(40),
    unitofmeasurement character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 303 (class 1259 OID 89265)
-- Name: pdflvltbl; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE pdflvltbl (
    pdflvltbl_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codeid character varying(60),
    txtdescr character varying(4000),
    codedistver character varying(36),
    uomdistver character varying(28),
    txtrmk character varying(4000),
    standardicao character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 304 (class 1259 OID 89273)
-- Name: proctransiction; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE proctransiction (
    proctransiction_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    transitionid character varying(20),
    type character varying(40),
    instruction character varying(4000),
    vectorheading double precision,
    ssa_pk double precision,
    tlof_pk double precision,
    rwydirection_pk double precision,
    haccuracyuom character varying(40),
    haccuracy double precision,
    geom geometry(MultiLineString,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 305 (class 1259 OID 89281)
-- Name: proctransleg; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE proctransleg (
    proctransleg_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    seqnumberarinc double precision,
    proctransiction_pk double precision,
    ssaseg_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 306 (class 1259 OID 89286)
-- Name: prroute; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE prroute (
    prroute_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    tofixfea_pk double precision,
    bltype character varying(8),
    tofix_pk double precision,
    intlfixfea_pk double precision,
    intlfix_pk double precision,
    dstfixfea_pk double precision,
    dstfix_pk double precision,
    mapid double precision,
    customercode character varying(12),
    routeident character varying(40),
    seq double precision,
    useind character varying(8),
    viacode character varying(12),
    ssawyident character varying(24),
    areacode character varying(12),
    levl character varying(20),
    routetype character varying(4),
    minalt character varying(20),
    maxalt character varying(20),
    timecode character varying(4),
    acftusegrp1 character varying(4),
    acftusegrp2 character varying(4),
    dirrst character varying(4),
    altdesc character varying(4),
    alt1 character varying(20),
    alt2 character varying(20),
    timeind character varying(4),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 307 (class 1259 OID 89294)
-- Name: prroutetime; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE prroutetime (
    prroutetime_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    prroute_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    seq double precision,
    timeofoper character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 308 (class 1259 OID 89299)
-- Name: radarcomponent; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE radarcomponent (
    radarcomponent_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    collocationgroup double precision,
    radarequipment_pk double precision,
    radarsystem_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 309 (class 1259 OID 89304)
-- Name: radarequipment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE radarequipment (
    radarequipment_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    monopulse character varying(40),
    precapprocradartyp character varying(40),
    slope double precision,
    slopeaccuracy double precision,
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    latitude double precision,
    longitude double precision,
    helprange double precision,
    magneticvariation double precision,
    helprangeuom character varying(40),
    helprangeaccuracy double precision,
    helprangeaccuom character varying(40),
    countrycode character varying(20),
    sac double precision,
    sic double precision,
    northreference double precision,
    model character varying(120),
    signaltype character varying(40),
    remark character varying(4000),
    specifictype character varying(120),
    ident character varying(240),
    name character varying(240),
    serialnumber character varying(64),
    range double precision,
    rangeuom character varying(40),
    rangeaccuracy double precision,
    rangeaccuracyuom character varying(40),
    dualchannel character varying(40),
    movingtargetind character varying(40),
    standbypower character varying(40),
    digital character varying(40),
    militaryuseonly character varying(40),
    specialuseonly character varying(40),
    specialacftonly character varying(40),
    magnvaraccuracy double precision,
    magnvardate character varying(16),
    vertcoveraltitude double precision,
    vertcoveraltuom character varying(40),
    vertcoverdistance double precision,
    vertcoverdistuom character varying(40),
    vertcoverazimuth double precision,
    antennatiltfixed character varying(40),
    tiltangle double precision,
    arts character varying(64),
    type character varying(40),
    transponder character varying(40),
    autonomous character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 310 (class 1259 OID 89312)
-- Name: radarsysrwylink; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE radarsysrwylink (
    radarsysrwylink_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtrmk character varying(4000),
    runway_pk double precision,
    radarsystem_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 311 (class 1259 OID 89320)
-- Name: radarsystem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE radarsystem (
    radarsystem_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    terrainmonitor character varying(40),
    broadcastid character varying(64),
    elevation double precision,
    elevationuom character varying(40),
    orgauth_pk double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    latitude double precision,
    longitude double precision,
    airport_pk double precision,
    model character varying(240),
    type character varying(40),
    ident character varying(240),
    geoidundulation double precision,
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 312 (class 1259 OID 89328)
-- Name: radiosonde; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE radiosonde (
    radiosonde_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    airspace_pk double precision,
    unit_pk double precision,
    txtloc character varying(240),
    valeet double precision,
    valweight double precision,
    vallen double precision,
    uomlen character varying(28),
    valascent double precision,
    uomascent character varying(40),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 313 (class 1259 OID 89336)
-- Name: reflector; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE reflector (
    reflector_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    type character varying(40),
    radarequipment_pk double precision,
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    latitude double precision,
    longitude double precision,
    verticaldatum character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 314 (class 1259 OID 89344)
-- Name: road; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE road (
    road_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    uomauwweight character varying(40),
    uomsiwltirepress character varying(40),
    uomsiwlweight character varying(40),
    airport_pk double precision,
    gatestand_pk double precision,
    ident character varying(240),
    status character varying(64),
    type character varying(28),
    abandoned character varying(40),
    s_elev double precision,
    s_elevuom character varying(40),
    s_geoid double precision,
    s_geoiduom character varying(40),
    s_vertdatum character varying(40),
    s_vertacc double precision,
    s_vertaccuom character varying(40),
    s_hacc double precision,
    s_haccuom character varying(40),
    auwweight double precision,
    codecomposition character varying(52),
    codecondsfc character varying(40),
    codepcnmaxtirepres character varying(40),
    lcnclass double precision,
    pcnclass double precision,
    pcnevalmethod character varying(40),
    pcnpavementsubgrad character varying(40),
    pcnpavementtype character varying(40),
    preparation character varying(44),
    siwltirepress double precision,
    siwlweight double precision,
    geom geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 315 (class 1259 OID 89352)
-- Name: rteportion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rteportion (
    rteportion_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airway_pk double precision,
    startfixfea_pk double precision,
    startfix_pk double precision,
    endfixfea_pk double precision,
    endfix_pk double precision,
    updateoper character varying(60),
    bltype character varying(8),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    passthrough_pk double precision,
    passthroughfea_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 316 (class 1259 OID 89357)
-- Name: rteseguse; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rteseguse (
    rteseguse_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airwayseg_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    coderteavbl character varying(60),
    noseq double precision,
    codedir character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    cardinaldirection character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 317 (class 1259 OID 89365)
-- Name: rteseguselvl; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rteseguselvl (
    rteseguselvl_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    rteseguse_pk double precision,
    pdflvlcol_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    valdistverlower double precision,
    uomdistverlower character varying(40),
    codedistverlower character varying(40),
    valdistverupper double precision,
    uomdistverupper character varying(40),
    codedistverupper character varying(40),
    altinterpretation character varying(48),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 318 (class 1259 OID 89370)
-- Name: runway; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE runway (
    runway_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    airport_pk double precision,
    type character varying(40),
    ident character varying(64),
    runwaylength double precision,
    nominallengthuom character varying(40),
    lengthaccuracy double precision,
    lengthaccuracyuom character varying(40),
    width double precision,
    nominalwidthuom character varying(40),
    widthaccuracy double precision,
    widthaccuracyuom character varying(40),
    widthshoulder double precision,
    widthshoulderuom character varying(40),
    lenstrip double precision,
    lengthstripuom character varying(40),
    widstrip double precision,
    widthstripuom character varying(40),
    lenoffset double precision,
    lengthoffsetuom character varying(40),
    widoffset double precision,
    widthoffsetuom character varying(40),
    codests character varying(64),
    abandoned character varying(40),
    rwydesc character varying(4000),
    rwydesc_lcl character varying(4000),
    txtprofile character varying(4000),
    txtprofile_lcl character varying(4000),
    txtmarking character varying(4000),
    txtmarking_lcl character varying(4000),
    txtrmk character varying(4000),
    txtrmk_lcl character varying(4000),
    surface character varying(52),
    preparation character varying(44),
    codecondsfc character varying(40),
    pcnclass double precision,
    pcnpavementtype character varying(40),
    pcnpavementsubgrad character varying(40),
    codepcnmaxtirepres character varying(40),
    valpcnmaxtirepres double precision,
    pcnevalmethod character varying(40),
    lcnclass double precision,
    siwlweight double precision,
    uomsiwlweight character varying(40),
    siwltirepress double precision,
    uomsiwltirepress character varying(40),
    auwweight double precision,
    uomauwweight character varying(40),
    pcnnote character varying(4000),
    valcrc character varying(32),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 319 (class 1259 OID 89378)
-- Name: rwdals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwdals (
    rwdals_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    rwydirection_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    vallen double precision,
    uomlen character varying(40),
    codeintst character varying(20),
    txtdscrflash character varying(4000),
    txtrmk_lcl character varying(4000),
    txtrmk character varying(4000),
    txtdscrflash_lcl character varying(4000),
    codeseqflash character varying(20),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 320 (class 1259 OID 89386)
-- Name: rwddecdist; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwddecdist (
    rwddecdist_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    codedayperiod character varying(20),
    valdist double precision,
    uomdist character varying(40),
    txtrmk character varying(4000),
    startpoint character varying(8),
    valcrc character varying(32),
    distanceaccuracy double precision,
    parent_pk double precision,
    parentfea_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 321 (class 1259 OID 89394)
-- Name: rwdlgtgrp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwdlgtgrp (
    rwdlgtgrp_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    rwdlgtsys_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    nolen double precision,
    vallenspacing double precision,
    nowid double precision,
    valwidspacing double precision,
    uomdist character varying(40),
    codesymmetry character varying(20),
    codeintst character varying(20),
    codecolour character varying(20),
    txtrmk character varying(4000),
    txtrmk_lcl character varying(4000),
    codetypelgt character varying(24),
    rwdlgtsysfea_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 322 (class 1259 OID 89402)
-- Name: rwdlgtsys; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwdlgtsys (
    rwdlgtsys_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    rwydirection_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codepsn character varying(48),
    txtdescr character varying(4000),
    txtdescremerg character varying(4000),
    txtrmk character varying(4000),
    txtdescr_lcl character varying(4000),
    txtrmk_lcl character varying(4000),
    codeintst character varying(20),
    codecolour character varying(20),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 323 (class 1259 OID 89410)
-- Name: rwdprtarea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwdprtarea (
    rwdprtarea_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    rwydirection_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    valwid double precision,
    vallen double precision,
    codecomposition character varying(52),
    codests character varying(64),
    txtlgt character varying(4000),
    txtrmk character varying(4000),
    valcrc character varying(32),
    preparation character varying(44),
    pcnclass double precision,
    pcnpavementtype character varying(40),
    pcnpavementsubgrad character varying(40),
    codepcnmaxtirepres character varying(40),
    valpcnmaxtirepres double precision,
    pcnevalmethod character varying(40),
    pcnnote character varying(4000),
    lcnclass double precision,
    siwlweight double precision,
    uomsiwlweight character varying(40),
    siwltirepress double precision,
    uomsiwltirepress character varying(40),
    auwweight double precision,
    uomauwweight character varying(40),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    lengthuom character varying(40),
    lighting character varying(40),
    obstaclefree character varying(40),
    surfacecondition character varying(40),
    vertaccuom character varying(40),
    verticalaccuracy double precision,
    verticaldatum character varying(40),
    widthuom character varying(40),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 324 (class 1259 OID 89418)
-- Name: rwdprtarea_geom; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwdprtarea_geom (
    rwdprtarea_geom_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    radiusuom character varying(40),
    radius double precision,
    origlon double precision,
    origlat double precision,
    rwdprtarea_pk double precision,
    longitude double precision,
    latitude double precision,
    seq double precision,
    path character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 325 (class 1259 OID 89423)
-- Name: rwyblastpad; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwyblastpad (
    rwyblastpad_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    rwydirection_pk double precision,
    s_haccuom character varying(40),
    auwweight double precision,
    codecomposition character varying(52),
    codecondsfc character varying(40),
    codepcnmaxtirepres character varying(40),
    lcnclass double precision,
    pcnclass double precision,
    pcnevalmethod character varying(40),
    pcnpavementsubgrad character varying(40),
    pcnpavementtype character varying(40),
    preparation character varying(44),
    siwltirepress double precision,
    siwlweight double precision,
    uomauwweight character varying(40),
    uomsiwltirepress character varying(40),
    uomsiwlweight character varying(40),
    s_vertaccuom character varying(40),
    s_vertacc double precision,
    s_vertdatum character varying(40),
    s_geoiduom character varying(40),
    s_geoid double precision,
    s_elevuom character varying(40),
    s_elev double precision,
    status character varying(64),
    length double precision,
    s_hacc double precision,
    geom geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 326 (class 1259 OID 89431)
-- Name: rwyclinept; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwyclinept (
    rwyclinept_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    uomdistver character varying(40),
    valcrc character varying(32),
    txtverdatum character varying(40),
    txtrmk character varying(4000),
    txtrmk_lcl character varying(4000),
    designator character varying(64),
    geoidundulationuom character varying(40),
    role character varying(64),
    rwydirection_pk double precision,
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 327 (class 1259 OID 89439)
-- Name: rwydirection; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwydirection (
    rwydirection_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    runway_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    rwyendid character varying(64),
    truebrg double precision,
    truebrgsrc character varying(20),
    magbrg double precision,
    threshlat double precision,
    threshlon double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    elevaccuracy double precision,
    geoidundulation double precision,
    uomdistver character varying(40),
    valcrc character varying(32),
    verdatum character varying(40),
    gradient double precision,
    tdzelevloc character varying(4),
    tdzelev double precision,
    threshelev double precision,
    elevtdzaccuracy double precision,
    tch double precision,
    vasis character varying(40),
    vasisangle double precision,
    durtax double precision,
    meht double precision,
    uommeht character varying(40),
    descrarstdvc character varying(4000),
    descrrvr character varying(4000),
    vfrpattern character varying(40),
    txtrmk character varying(4000),
    uomtch character varying(28),
    txtrmk_lcl character varying(4000),
    codepsnvasis character varying(40),
    noboxvasis double precision,
    codeportablevasis character varying(40),
    apprmarkingcond character varying(40),
    apprmarkingtype character varying(56),
    auwweight double precision,
    bpadlength double precision,
    bpadstatus character varying(64),
    classlightingjar character varying(40),
    codecomposition character varying(52),
    codecondsfc character varying(40),
    codepcnmaxtirepres character varying(40),
    colour character varying(40),
    elevationtdzuom character varying(40),
    elevtdzaccuom character varying(40),
    emergencylighting character varying(40),
    intensitylevel character varying(52),
    lcnclass double precision,
    pcnclass double precision,
    pcnevalmethod character varying(40),
    pcnpavementsubgrad character varying(40),
    pcnpavementtype character varying(40),
    precapprochguid character varying(88),
    preparation character varying(44),
    rwyelement_pk double precision,
    geoiduom character varying(40),
    vertaccuom character varying(40),
    siwltirepress double precision,
    siwlweight double precision,
    slopetdz double precision,
    truebearingacc double precision,
    uomauwweight character varying(40),
    uomsiwltirepress character varying(40),
    uomsiwlweight character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    uuid character varying
);


--
-- TOC entry 328 (class 1259 OID 89447)
-- Name: rwydirectnstr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwydirectnstr (
    rwydirectnstr_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    rwydirection_pk double precision,
    ssa_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 329 (class 1259 OID 89455)
-- Name: rwyelement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE rwyelement (
    rwyelement_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    runway_pk double precision,
    type character varying(48),
    noseq double precision,
    length double precision,
    lengthuom character varying(40),
    width double precision,
    widthuom character varying(40),
    gradeseparation character varying(40),
    composition character varying(52),
    preparation character varying(44),
    surfacecondition character varying(40),
    classpcn double precision,
    pavementtypepcn character varying(40),
    pavesubgradepcn character varying(40),
    maxtyrepressurepcn character varying(40),
    evalmethodpcn character varying(40),
    classlcn double precision,
    weightsiwl double precision,
    weightsiwluom character varying(40),
    tyrepressuresiwl double precision,
    tyrepresssiwluom character varying(40),
    weightauw double precision,
    weightauwuom character varying(40),
    txtrmk character varying(4000),
    elevation double precision,
    elevationuom character varying(40),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 330 (class 1259 OID 89463)
-- Name: sdf; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sdf (
    sdf_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    frequency double precision,
    frequencyuom character varying(40),
    orgauth_pk double precision,
    longitude double precision,
    latitude double precision,
    txtrmk character varying(4000),
    haccuracyuom character varying(40),
    haccuracy double precision,
    vertaccuom character varying(40),
    verticalaccuracy double precision,
    verticaldatum character varying(40),
    geoidundulationuom character varying(40),
    geoidundulation double precision,
    elevationuom character varying(40),
    elevation double precision,
    flightchecked character varying(40),
    datemagvar timestamp without time zone,
    magvaracc double precision,
    magneticvariation double precision,
    mobile character varying(40),
    emissionclass character varying(40),
    name character varying(240),
    designator character varying(16),
    magneticbearing double precision,
    truebearing double precision,
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 331 (class 1259 OID 89471)
-- Name: segmnt; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE segmnt (
    segmnt_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    fromfixfea_pk double precision,
    fromfix_pk double precision,
    tofixfea_pk double precision,
    tofix_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    bltype character varying(8),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    codetype character varying(20),
    codesource character varying(20),
    txtrmk_lcl character varying(4000),
    valtruetrack double precision,
    valmagtrack double precision,
    valrevtruetrack double precision,
    valrevmagtrack double precision,
    vallen double precision,
    uomdist character varying(40),
    txtrmk character varying(4000),
    ff_flyover character varying(40),
    ff_radarguidance character varying(40),
    ff_reportingatc character varying(40),
    ff_rolefreeflight character varying(40),
    ff_rolemiltraining character varying(40),
    ff_rolervsm character varying(40),
    ff_turnraidus double precision,
    ff_turnraidusuom character varying(40),
    ff_waypoint character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    tf_flyover character varying(40),
    tf_radarguidance character varying(40),
    tf_reportingatc character varying(40),
    tf_rolefreeflight character varying(40),
    tf_rolemiltraining character varying(40),
    tf_rolervsm character varying(40),
    tf_turnraidus double precision,
    tf_turnraidusuom character varying(40),
    tf_waypoint character varying(40),
    geom geometry(MultiLineString,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 332 (class 1259 OID 89479)
-- Name: service; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE service (
    service_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    unit_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    typ character varying(56),
    noseq double precision,
    codesource character varying(20),
    geolat double precision,
    geolong double precision,
    txtrmk_lcl character varying(4000),
    coderadar character varying(40),
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    areacode character varying(12),
    magvariation double precision,
    magvardate timestamp without time zone,
    remotesitenm character varying(100),
    txtrmkworkh_lcl character varying(4000),
    complianticao character varying(40),
    datalink character varying(40),
    datalinkchannel character varying(40),
    datalinkenabled character varying(40),
    elevationuom character varying(40),
    flightoperations character varying(40),
    geoidundulationuom character varying(40),
    name character varying(240),
    rank character varying(40),
    recorded character varying(40),
    vertaccuom character varying(40),
    voice character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 333 (class 1259 OID 89487)
-- Name: sigpointinas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sigpointinas (
    sigpointinas_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    sigpointfea_pk double precision,
    sigpoint_pk double precision,
    airspace_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    bltype character varying(8),
    mslink double precision,
    mapid double precision,
    codetype character varying(20),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 383 (class 1259 OID 103592)
-- Name: sm_difference; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_difference (
    diff_pk double precision NOT NULL,
    fea_pk integer NOT NULL,
    fea_nr integer NOT NULL,
    occurrence_pk double precision NOT NULL,
    attr_pk double precision,
    attr_db_name character varying(72),
    attr_value character varying(80),
    continuation integer NOT NULL,
    source_pk double precision NOT NULL,
    transaction_date timestamp without time zone NOT NULL,
    effect_date timestamp without time zone NOT NULL,
    transactn character varying(4) NOT NULL,
    sm_users_pk integer NOT NULL,
    linked timestamp without time zone,
    latsec double precision,
    lonsec double precision,
    end_date timestamp without time zone,
    sm_status character varying(4) NOT NULL,
    type_ind character varying(4) NOT NULL,
    temp_seq double precision NOT NULL
);


--
-- TOC entry 400 (class 1259 OID 103670)
-- Name: sm_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE sm_product (
    product_name text NOT NULL,
    product_remark text
);


--
-- TOC entry 334 (class 1259 OID 89495)
-- Name: specialdate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE specialdate (
    specialdate_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    orgauth_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(24),
    dateyear double precision,
    datemonth character varying(20),
    dateday double precision,
    txtname character varying(240),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 335 (class 1259 OID 89503)
-- Name: specnavstn; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE specnavstn (
    specnavstn_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    specnavsys_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtname character varying(240),
    codetypeser character varying(48),
    valfreq double precision,
    uomfreq character varying(40),
    codeem character varying(40),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    orgauth_pk double precision,
    valgeoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    elevationuom character varying(40),
    geoidundulationuom character varying(40),
    vertaccuom character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 336 (class 1259 OID 89511)
-- Name: specnavsys; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE specnavsys (
    specnavsys_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    orgauth_pk double precision,
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    codeid character varying(16),
    txtname character varying(240),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 337 (class 1259 OID 89519)
-- Name: ssa; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ssa (
    ssa_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    surface_pk double precision,
    surfacefea_pk double precision,
    msagroup_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    bltype character varying(8),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    typ character varying(8),
    ident character varying(28),
    codernp double precision,
    txtdescrcomfail character varying(4000),
    codetyperte character varying(40),
    txtdescrmiss character varying(4000),
    txtrmk character varying(4000),
    gpsfmsind character varying(4),
    procedurecat character varying(16),
    addequipment character varying(44),
    approachprefix character varying(40),
    approachtype character varying(40),
    channelgnss double precision,
    circlingidentif character varying(40),
    codingstandard character varying(48),
    contingencyroute character varying(40),
    coptertrack double precision,
    courcereversalinst character varying(4000),
    designcriteria character varying(48),
    flightchecked character varying(40),
    instruction character varying(4000),
    msagroup_pk2 double precision,
    multipleidentif character varying(40),
    name character varying(240),
    waasreliable character varying(40),
    codecatacft character varying(24),
    geom geometry(MultiLineString,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 338 (class 1259 OID 89530)
-- Name: ssapath; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ssapath (
    ssapath_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    seq double precision,
    name character varying(240),
    ssa_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 339 (class 1259 OID 89535)
-- Name: ssapathleg; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ssapathleg (
    ssapathleg_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    ssapath_pk double precision,
    seq double precision,
    ssaseg_pk double precision,
    geom geometry(MultiLineString,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 340 (class 1259 OID 89543)
-- Name: ssapathlegarea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ssapathlegarea (
    ssapathlegarea_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    obsassessarea_pk double precision,
    ssapathleg_pk double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 341 (class 1259 OID 89548)
-- Name: ssaseg; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ssaseg (
    ssaseg_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    fix_pk double precision,
    fixfea_pk double precision,
    bltype character varying(8),
    mapid double precision,
    parentfea_pk double precision,
    parent_pk double precision,
    noseq double precision,
    codephase character varying(20),
    pathterm character varying(20),
    transident character varying(20),
    coderolefix character varying(40),
    centerfea_pk double precision,
    center_pk double precision,
    recfacfea_pk double precision,
    recfac_pk double precision,
    coderepatc character varying(40),
    turndirectn character varying(40),
    turnrequired character varying(40),
    valbankangle double precision,
    arcradius double precision,
    theta double precision,
    rho double precision,
    uomdisthorz character varying(40),
    outcourse double precision,
    codetypecourse character varying(40),
    incourse double precision,
    calcin double precision,
    calcout double precision,
    legdistance double precision,
    legtime double precision,
    uomdur character varying(40),
    calcdis double precision,
    altind character varying(48),
    codedistverlower character varying(40),
    uomdistverlower character varying(40),
    alt1 double precision,
    codedistverupper character varying(40),
    uomdistverupper character varying(40),
    alt2 double precision,
    atcindicator character varying(4),
    speedlim double precision,
    uomspeed character varying(40),
    codespeedref character varying(40),
    vertangle double precision,
    proctrntyp character varying(4),
    nonstanreqd character varying(20),
    nonstanradius double precision,
    txtrmk character varying(4000),
    altoverrideatc double precision,
    altoverrideatcuom character varying(40),
    altoverrideref character varying(40),
    course double precision,
    courseclinedist double precision,
    courseclinedistuom character varying(40),
    courseclineinter character varying(40),
    coursedirection character varying(40),
    courseoffsetangle double precision,
    courseoffsetdis double precision,
    courseoffsetdisuom character varying(40),
    courseoffsetside character varying(40),
    endconddesig character varying(40),
    guidancesystem character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    heightmapt double precision,
    heightmaptuom character varying(40),
    indicatorfacf character varying(40),
    landingsystemcat character varying(88),
    leaddme double precision,
    leaddmeuom character varying(40),
    leadradial double precision,
    legpath character varying(40),
    legtypearinc character varying(40),
    minbarovnavtemp double precision,
    minbarovnavtempuom character varying(40),
    minobsclearalt double precision,
    minobsclearaltuom character varying(40),
    missedapprlegtyp character varying(40),
    procturnrequired character varying(40),
    radarguidance character varying(40),
    reqnavigationperf double precision,
    rnpdmeauthorized character varying(40),
    segmentlegtype character varying(120),
    speedinterpr character varying(48),
    thrshaftermapt character varying(40),
    waypoint character varying(40),
    valcrc character varying(32),
    navsysdistind_pk double precision,
    navsysangind_pk double precision,
    geom geometry(MultiLineString,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 342 (class 1259 OID 89556)
-- Name: ssausage; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE ssausage (
    ssausage_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    ssa_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    coderteavbl character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 343 (class 1259 OID 89564)
-- Name: surveyctrlpt; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE surveyctrlpt (
    surveyctrlpt_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    verticalaccuracy double precision,
    verticaldatum character varying(40),
    geoidundulationuom character varying(40),
    geoidundulation double precision,
    elevationuom character varying(40),
    elevation double precision,
    longitude double precision,
    latitude double precision,
    role character varying(40),
    ident character varying(240),
    airport_pk double precision,
    vertaccuom character varying(40),
    haccuracyuom character varying(40),
    haccuracy double precision,
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 344 (class 1259 OID 89572)
-- Name: svcinasp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE svcinasp (
    svcinasp_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    service_pk double precision,
    airspace_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 345 (class 1259 OID 89580)
-- Name: svconadhp; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE svconadhp (
    svconadhp_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    service_pk double precision,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 346 (class 1259 OID 89588)
-- Name: svconhldgproc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE svconhldgproc (
    svconhldgproc_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    service_pk double precision,
    holdingproc_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 347 (class 1259 OID 89596)
-- Name: svconrteseg; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE svconrteseg (
    svconrteseg_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    service_pk double precision,
    airwayseg_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 348 (class 1259 OID 89604)
-- Name: svconssa; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE svconssa (
    svconssa_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    service_pk double precision,
    ssa_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 350 (class 1259 OID 89620)
-- Name: tacanlimitatn; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tacanlimitatn (
    tacanlimitatn_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    tacan_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    valanglefm double precision,
    valangleto double precision,
    valdistinner double precision,
    valdstverlwr double precision,
    valdistouter double precision,
    valdistverupper double precision,
    uomdistverupper character varying(40),
    codedistverupper character varying(40),
    uomdistverlower character varying(40),
    codedistverlower character varying(40),
    valanglescallop double precision,
    txtrmk character varying(4000),
    angledirref character varying(40),
    angletype character varying(40),
    arcdirection character varying(40),
    innerdistanceuom character varying(40),
    outerdistanceuom character varying(40),
    signaltype character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 351 (class 1259 OID 89628)
-- Name: tfcflowrestr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tfcflowrestr (
    tfcflowrestr_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codeid character varying(36),
    codetype character varying(4),
    txtoprgoal character varying(4000),
    txtdescr character varying(4000),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 352 (class 1259 OID 89636)
-- Name: tfcflowrte; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tfcflowrte (
    tfcflowrte_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    tfcflowrestr_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    noseq double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 353 (class 1259 OID 89641)
-- Name: tfcflwrtelem; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tfcflwrtelem (
    tfcflwrtelem_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    tfcflowrte_pk double precision,
    parentfea_pk double precision,
    parent_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    bltype character varying(8),
    mslink double precision,
    mapid double precision,
    noseq double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 354 (class 1259 OID 89646)
-- Name: tfcflwrtellv; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tfcflwrtellv (
    tfcflwrtellv_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    tfcflwrtelem_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valdistverlower double precision,
    uomdistverlower character varying(28),
    codedistverlower character varying(36),
    valdistverupper double precision,
    uomdistverupper character varying(40),
    codedistverupper character varying(36),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 355 (class 1259 OID 89651)
-- Name: time_template; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE time_template (
    time_template_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    templ_desc character varying(960),
    latitude double precision,
    longitude double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 356 (class 1259 OID 89659)
-- Name: timesheet; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE timesheet (
    timesheet_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    parentfea_pk double precision,
    parent_pk double precision,
    monthvalidwef character varying(20),
    dayvalidwef double precision,
    codeday character varying(48),
    hrvalidwef double precision,
    minvalidwef double precision,
    codeeventwef character varying(40),
    codetimeref character varying(40),
    monthvalidtil character varying(20),
    dayvalidtil double precision,
    codedaytil character varying(48),
    timereleventwef double precision,
    codecombwef character varying(40),
    hourtil double precision,
    minutetil double precision,
    codeeventtil character varying(40),
    timereleventtil double precision,
    codecombtil character varying(40),
    daylightsavingadj character varying(40),
    excluded character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 357 (class 1259 OID 89667)
-- Name: tlof; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tlof (
    tlof_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtdesig character varying(64),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    valgeoaccuracy double precision,
    uomgeoaccuracy character varying(40),
    valelev double precision,
    valelevaccuracy double precision,
    valgoidundulation double precision,
    valcrc character varying(32),
    txtverdatum character varying(40),
    vallen double precision,
    valwid double precision,
    valslope double precision,
    codecomposition character varying(52),
    codecondsfc character varying(40),
    codeclasshel character varying(40),
    txtmarking character varying(4000),
    codests character varying(64),
    txtrmk character varying(4000),
    preparation character varying(44),
    pcnclass double precision,
    pcnpavementtype character varying(40),
    pcnpavementsubgrad character varying(40),
    codepcnmaxtirepres character varying(40),
    valpcnmaxtirepres double precision,
    pcnevalmethod character varying(40),
    pcnnote character varying(4000),
    lcnclass double precision,
    siwlweight double precision,
    uomsiwlweight character varying(40),
    siwltirepress double precision,
    uomsiwltirepress character varying(40),
    auwweight double precision,
    uomauwweight character varying(40),
    abandoned character varying(40),
    elevationuom character varying(40),
    geoidundulationuom character varying(40),
    lengthuom character varying(40),
    runway_pk double precision,
    s_elev double precision,
    s_elevuom character varying(40),
    s_geoid double precision,
    s_geoiduom character varying(40),
    s_hacc double precision,
    s_haccuom character varying(40),
    s_vertacc double precision,
    s_vertaccuom character varying(40),
    s_vertdatum character varying(40),
    vertaccuom character varying(40),
    widthuom character varying(40),
    geom geometry(Point,4326),
    geom_s geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 358 (class 1259 OID 89675)
-- Name: tlof_geom; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tlof_geom (
    tlof_geom_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    tlof_pk double precision,
    seq double precision,
    latitude double precision,
    longitude double precision,
    path character varying(40),
    origlat double precision,
    origlon double precision,
    radius double precision,
    radiusuom character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 359 (class 1259 OID 89680)
-- Name: tloflgtsys; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tloflgtsys (
    tloflgtsys_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    tlof_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codepsn character varying(48),
    txtdescr character varying(4000),
    txtdscremerg character varying(4000),
    txtrmk character varying(4000),
    codeintst character varying(20),
    codecolour character varying(20),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 360 (class 1259 OID 89688)
-- Name: tlofsafearea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tlofsafearea (
    tlofsafearea_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    tlof_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    valwid double precision,
    vallen double precision,
    codecomposition character varying(52),
    codests character varying(64),
    txtlgt character varying(4000),
    txtrmk character varying(4000),
    preparation character varying(44),
    pcnclass double precision,
    pcnpavementtype character varying(40),
    pcnpavementsubgrad character varying(40),
    codepcnmaxtirepres character varying(40),
    valpcnmaxtirepres double precision,
    pcnevalmethod character varying(40),
    pcnnote character varying(4000),
    lcnclass double precision,
    siwlweight double precision,
    uomsiwlweight character varying(40),
    siwltirepress double precision,
    uomsiwltirepress character varying(40),
    auwweight double precision,
    uomauwweight character varying(40),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    lengthuom character varying(40),
    lighting character varying(40),
    obstaclefree character varying(40),
    surfacecondition character varying(40),
    vertaccuom character varying(40),
    verticalaccuracy double precision,
    verticaldatum character varying(40),
    widthuom character varying(40),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 361 (class 1259 OID 89696)
-- Name: tlofsarea_geom; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE tlofsarea_geom (
    tlofsarea_geom_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    tlofsafearea_pk double precision,
    seq double precision,
    latitude double precision,
    longitude double precision,
    radiusuom character varying(40),
    origlat double precision,
    origlon double precision,
    radius double precision,
    path character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 362 (class 1259 OID 89701)
-- Name: twy; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE twy (
    twy_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    airport_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtdesig character varying(64),
    codetype character varying(44),
    valwid double precision,
    uomwid character varying(40),
    codecomposition character varying(52),
    codecondsfc character varying(40),
    codests character varying(64),
    txtmarking character varying(4000),
    txtrmk character varying(4000),
    valcrc character varying(32),
    preparation character varying(44),
    pcnclass double precision,
    pcnpavementtype character varying(40),
    pcnpavementsubgrad character varying(40),
    codepcnmaxtirepres character varying(40),
    valpcnmaxtirepres double precision,
    pcnevalmethod character varying(40),
    pcnnote character varying(4000),
    lcnclass double precision,
    siwlweight double precision,
    uomsiwlweight character varying(40),
    siwltirepress double precision,
    uomsiwltirepress character varying(40),
    auwweight double precision,
    uomauwweight character varying(40),
    abandoned character varying(40),
    length double precision,
    lengthuom character varying(40),
    widthshoulder double precision,
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 363 (class 1259 OID 89709)
-- Name: twyelement; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE twyelement (
    twyelement_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    tyrepressuresiwl double precision,
    tyrepresssiwluom character varying(40),
    weightauw double precision,
    weightauwuom character varying(40),
    txtrmk character varying(4000),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    verticaldatum character varying(40),
    verticalaccuracy double precision,
    vertaccuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    weightsiwluom character varying(40),
    weightsiwl double precision,
    classlcn double precision,
    evalmethodpcn character varying(40),
    maxtyrepressurepcn character varying(40),
    pavesubgradepcn character varying(40),
    pavementtypepcn character varying(40),
    classpcn double precision,
    surfacecondition character varying(40),
    preparation character varying(44),
    composition character varying(52),
    gradeseparation character varying(40),
    widthuom character varying(40),
    width double precision,
    lengthuom character varying(40),
    length double precision,
    noseq double precision,
    twy_pk double precision,
    type character varying(48),
    geom geometry(MultiPolygon,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 364 (class 1259 OID 89717)
-- Name: twyhldpos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE twyhldpos (
    twyhldpos_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    txtmarking character varying(4000),
    txtlgt character varying(4000),
    txtrmk character varying(4000),
    txtrmk_lcl character varying(4000),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    guidanceline_pk double precision,
    haccuracy double precision,
    haccuracyuom character varying(40),
    landingcategory character varying(52),
    latitude double precision,
    longitude double precision,
    runway_pk double precision,
    status character varying(64),
    twy_pk double precision,
    vertaccuom character varying(40),
    verticalaccuracy double precision,
    verticaldatum character varying(40),
    geom geometry(Point,4326),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 365 (class 1259 OID 89725)
-- Name: twyintrsectn; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE twyintrsectn (
    twyintrsectn_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    twy_pk double precision,
    intrsctionfea_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    intrsction_pk double precision,
    txtrmk character varying(4000),
    txtrmk_lcl character varying(4000),
    valcrc character varying(32),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 366 (class 1259 OID 89733)
-- Name: twylgtsys; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE twylgtsys (
    twylgtsys_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    twy_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codepsn character varying(48),
    txtdescr character varying(4000),
    txtdescremerg character varying(4000),
    txtrmk character varying(4000),
    txtdescr_lcl character varying(4000),
    codeintst character varying(20),
    codecolour character varying(20),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 367 (class 1259 OID 89741)
-- Name: unit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE unit (
    unit_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    orgauth_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    airport_pk double precision,
    txtname character varying(240),
    codetype character varying(40),
    codeclass character varying(40),
    geolat double precision,
    geolong double precision,
    codedatum character varying(12),
    txtrmk character varying(4000),
    codeid character varying(48),
    elevation double precision,
    elevationuom character varying(40),
    geoidundulation double precision,
    geoidundulationuom character varying(40),
    haccuracy double precision,
    haccuracyuom character varying(40),
    military character varying(40),
    vertaccuom character varying(40),
    verticalaccuracy double precision,
    verticaldatum character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 368 (class 1259 OID 89752)
-- Name: unitaddress; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE unitaddress (
    unitaddress_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    unit_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(32),
    noseq double precision,
    txtaddress character varying(4000),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 369 (class 1259 OID 89760)
-- Name: unitassoc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE unitassoc (
    unitassoc_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    unit_pk double precision,
    unit_pk2 double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    bltype character varying(8),
    mapid double precision,
    noseq double precision,
    codetype character varying(40),
    codeworkhr character varying(20),
    txtrmkworkhr character varying(4000),
    txtrmk character varying(4000),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 371 (class 1259 OID 89776)
-- Name: vorlimitation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE vorlimitation (
    vorlimitation_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    vor_pk double precision,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    codetype character varying(40),
    valanglefm double precision,
    valangleto double precision,
    valdistinner double precision,
    valdistverlower double precision,
    valdistouter double precision,
    valdistverupper double precision,
    uomdistverupper character varying(40),
    codedistverupper character varying(40),
    uomdistverlower character varying(40),
    codedistverlower character varying(40),
    valanglescallop double precision,
    txtrmk character varying(4000),
    angledirref character varying(40),
    angletype character varying(40),
    arcdirection character varying(40),
    innerdistanceuom character varying(40),
    outerdistanceuom character varying(40),
    signaltype character varying(40),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);


--
-- TOC entry 373 (class 1259 OID 89792)
-- Name: workarea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE workarea (
    workarea_pk double precision NOT NULL,
    effectivedt timestamp without time zone,
    updatedt timestamp without time zone,
    updateoper character varying(60),
    source character varying(64),
    diffind character varying(4),
    mslink double precision,
    mapid double precision,
    bltype character varying(8),
    type character varying(48),
    plannoper timestamp without time zone,
    airport_pk double precision,
    s_elev double precision,
    s_elevuom character varying(40),
    s_geoid double precision,
    isactive character varying(40),
    s_vertdatum character varying(40),
    s_vertacc double precision,
    s_vertaccuom character varying(40),
    s_hacc double precision,
    s_haccuom character varying(40),
    s_geoiduom character varying(40),
    geom geometry(GeometryCollection),
    validfrom timestamp without time zone DEFAULT now() NOT NULL,
    validto timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL
);

--
-- TOC entry 4569 (class 2604 OID 112121)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY boundingbox ALTER COLUMN id SET DEFAULT nextval('boundingbox_id_seq'::regclass);


--
-- TOC entry 4540 (class 2604 OID 103547)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_attr ALTER COLUMN id1 SET DEFAULT nextval('"SM_ATTR_ID1_seq"'::regclass);


--
-- TOC entry 4541 (class 2604 OID 103558)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_attr_d ALTER COLUMN id1 SET DEFAULT nextval('"SM_ATTR_D_ID1_seq"'::regclass);


--
-- TOC entry 4542 (class 2604 OID 103569)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_code ALTER COLUMN id1 SET DEFAULT nextval('"SM_CODE_ID1_seq"'::regclass);


--
-- TOC entry 4543 (class 2604 OID 103580)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_code_v ALTER COLUMN id1 SET DEFAULT nextval('"SM_CODE_V_ID1_seq"'::regclass);


--
-- TOC entry 4544 (class 2604 OID 103602)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_fea ALTER COLUMN id1 SET DEFAULT nextval('"SM_FEA_ID1_seq"'::regclass);


--
-- TOC entry 4545 (class 2604 OID 103610)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_fea_d ALTER COLUMN id1 SET DEFAULT nextval('"SM_FEA_D_ID1_seq"'::regclass);


--
-- TOC entry 4546 (class 2604 OID 103621)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_format ALTER COLUMN id1 SET DEFAULT nextval('"SM_FORMAT_ID1_seq"'::regclass);


--
-- TOC entry 4547 (class 2604 OID 103629)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_gen ALTER COLUMN id1 SET DEFAULT nextval('"SM_GEN_ID1_seq"'::regclass);


--
-- TOC entry 4548 (class 2604 OID 103637)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_general ALTER COLUMN id1 SET DEFAULT nextval('"SM_GENERAL_ID1_seq"'::regclass);


--
-- TOC entry 4549 (class 2604 OID 103645)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_geom ALTER COLUMN id1 SET DEFAULT nextval('"SM_GEOM_ID1_seq"'::regclass);


--
-- TOC entry 4550 (class 2604 OID 103656)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_primary_keys ALTER COLUMN id1 SET DEFAULT nextval('"SM_PRIMARY_KEYS_ID1_seq"'::regclass);


--
-- TOC entry 4551 (class 2604 OID 103664)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_prod_setting ALTER COLUMN id1 SET DEFAULT nextval('"SM_PROD_SETTING_ID1_seq"'::regclass);


--
-- TOC entry 4552 (class 2604 OID 103683)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_relations ALTER COLUMN id1 SET DEFAULT nextval('"SM_RELATIONS_ID1_seq"'::regclass);


--
-- TOC entry 4553 (class 2604 OID 103691)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_rolldate ALTER COLUMN id1 SET DEFAULT nextval('"SM_ROLLDATE_ID1_seq"'::regclass);


--
-- TOC entry 4554 (class 2604 OID 103699)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_source ALTER COLUMN id1 SET DEFAULT nextval('"SM_SOURCE_ID1_seq"'::regclass);


--
-- TOC entry 4556 (class 2604 OID 103711)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smf_families ALTER COLUMN id1 SET DEFAULT nextval('"SMF_FAMILIES_ID1_seq"'::regclass);


--
-- TOC entry 4557 (class 2604 OID 103719)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smf_filter ALTER COLUMN id1 SET DEFAULT nextval('"SMF_FILTER_ID1_seq"'::regclass);


--
-- TOC entry 4558 (class 2604 OID 103730)
-- Name: id4; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_db_check ALTER COLUMN id4 SET DEFAULT nextval('"SMU_DB_CHECK_ID4_seq"'::regclass);


--
-- TOC entry 4559 (class 2604 OID 103738)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_filter ALTER COLUMN id1 SET DEFAULT nextval('"SMU_FILTER_ID1_seq"'::regclass);


--
-- TOC entry 4560 (class 2604 OID 103746)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_filter_cond ALTER COLUMN id1 SET DEFAULT nextval('"SMU_FILTER_COND_ID1_seq"'::regclass);


--
-- TOC entry 4561 (class 2604 OID 103754)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_process ALTER COLUMN id1 SET DEFAULT nextval('"SMU_PROCESS_ID1_seq"'::regclass);


--
-- TOC entry 4562 (class 2604 OID 103765)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_role ALTER COLUMN id1 SET DEFAULT nextval('"SMU_ROLE_ID1_seq"'::regclass);


--
-- TOC entry 4563 (class 2604 OID 103773)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_role_filter ALTER COLUMN id1 SET DEFAULT nextval('"SMU_ROLE_FILTER_ID1_seq"'::regclass);


--
-- TOC entry 4564 (class 2604 OID 103781)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_role_process ALTER COLUMN id1 SET DEFAULT nextval('"SMU_ROLE_PROCESS_ID1_seq"'::regclass);


--
-- TOC entry 4565 (class 2604 OID 103789)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_role_source ALTER COLUMN id1 SET DEFAULT nextval('"SMU_ROLE_SOURCE_ID1_seq"'::regclass);


--
-- TOC entry 4567 (class 2604 OID 103798)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_user ALTER COLUMN id1 SET DEFAULT nextval('"SMU_USER_ID1_seq"'::regclass);


--
-- TOC entry 4568 (class 2604 OID 103809)
-- Name: id1; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_user_role ALTER COLUMN id1 SET DEFAULT nextval('"SMU_USER_ROLE_ID1_seq"'::regclass);


--
-- TOC entry 5084 (class 2606 OID 103541)
-- Name: HAS_ANNOTATION_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY has_annotation
    ADD CONSTRAINT "HAS_ANNOTATION_pkey" PRIMARY KEY (has_annotation_pk);


--
-- TOC entry 5120 (class 2606 OID 103713)
-- Name: SMF_FAMILIES_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smf_families
    ADD CONSTRAINT "SMF_FAMILIES_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5122 (class 2606 OID 103724)
-- Name: SMF_FILTER_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smf_filter
    ADD CONSTRAINT "SMF_FILTER_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5124 (class 2606 OID 103732)
-- Name: SMU_DB_CHECK_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_db_check
    ADD CONSTRAINT "SMU_DB_CHECK_pkey" PRIMARY KEY (id4);


--
-- TOC entry 5128 (class 2606 OID 103748)
-- Name: SMU_FILTER_COND_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_filter_cond
    ADD CONSTRAINT "SMU_FILTER_COND_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5126 (class 2606 OID 103740)
-- Name: SMU_FILTER_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_filter
    ADD CONSTRAINT "SMU_FILTER_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5130 (class 2606 OID 103759)
-- Name: SMU_PROCESS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_process
    ADD CONSTRAINT "SMU_PROCESS_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5134 (class 2606 OID 103775)
-- Name: SMU_ROLE_FILTER_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_role_filter
    ADD CONSTRAINT "SMU_ROLE_FILTER_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5136 (class 2606 OID 103783)
-- Name: SMU_ROLE_PROCESS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_role_process
    ADD CONSTRAINT "SMU_ROLE_PROCESS_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5138 (class 2606 OID 103791)
-- Name: SMU_ROLE_SOURCE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_role_source
    ADD CONSTRAINT "SMU_ROLE_SOURCE_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5132 (class 2606 OID 103767)
-- Name: SMU_ROLE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_role
    ADD CONSTRAINT "SMU_ROLE_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5142 (class 2606 OID 103811)
-- Name: SMU_USER_ROLE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_user_role
    ADD CONSTRAINT "SMU_USER_ROLE_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5140 (class 2606 OID 103803)
-- Name: SMU_USER_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smu_user
    ADD CONSTRAINT "SMU_USER_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5088 (class 2606 OID 103563)
-- Name: SM_ATTR_D_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_attr_d
    ADD CONSTRAINT "SM_ATTR_D_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5086 (class 2606 OID 103552)
-- Name: SM_ATTR_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_attr
    ADD CONSTRAINT "SM_ATTR_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5092 (class 2606 OID 103585)
-- Name: SM_CODE_V_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_code_v
    ADD CONSTRAINT "SM_CODE_V_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5090 (class 2606 OID 103574)
-- Name: SM_CODE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_code
    ADD CONSTRAINT "SM_CODE_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5094 (class 2606 OID 103596)
-- Name: SM_DIFFERENCE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_difference
    ADD CONSTRAINT "SM_DIFFERENCE_pkey" PRIMARY KEY (diff_pk);


--
-- TOC entry 5098 (class 2606 OID 103615)
-- Name: SM_FEA_D_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_fea_d
    ADD CONSTRAINT "SM_FEA_D_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5096 (class 2606 OID 103604)
-- Name: SM_FEA_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_fea
    ADD CONSTRAINT "SM_FEA_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5100 (class 2606 OID 103623)
-- Name: SM_FORMAT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_format
    ADD CONSTRAINT "SM_FORMAT_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5104 (class 2606 OID 103639)
-- Name: SM_GENERAL_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_general
    ADD CONSTRAINT "SM_GENERAL_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5102 (class 2606 OID 103631)
-- Name: SM_GEN_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_gen
    ADD CONSTRAINT "SM_GEN_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5106 (class 2606 OID 103650)
-- Name: SM_GEOM_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_geom
    ADD CONSTRAINT "SM_GEOM_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5108 (class 2606 OID 103658)
-- Name: SM_PRIMARY_KEYS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_primary_keys
    ADD CONSTRAINT "SM_PRIMARY_KEYS_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5112 (class 2606 OID 103677)
-- Name: SM_PRODUCT_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_product
    ADD CONSTRAINT "SM_PRODUCT_pkey" PRIMARY KEY (product_name);


--
-- TOC entry 5110 (class 2606 OID 103669)
-- Name: SM_PROD_SETTING_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_prod_setting
    ADD CONSTRAINT "SM_PROD_SETTING_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5114 (class 2606 OID 103685)
-- Name: SM_RELATIONS_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_relations
    ADD CONSTRAINT "SM_RELATIONS_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5116 (class 2606 OID 103693)
-- Name: SM_ROLLDATE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_rolldate
    ADD CONSTRAINT "SM_ROLLDATE_pkey" PRIMARY KEY (id1);


--
-- TOC entry 5118 (class 2606 OID 103704)
-- Name: SM_SOURCE_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sm_source
    ADD CONSTRAINT "SM_SOURCE_pkey" PRIMARY KEY (id1);


--
-- TOC entry 4572 (class 2606 OID 103833)
-- Name: acftclass_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY acftclass
    ADD CONSTRAINT acftclass_pkey PRIMARY KEY (acftclass_pk, validfrom, validto);


--
-- TOC entry 4575 (class 2606 OID 103852)
-- Name: adhpaddress_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adhpaddress
    ADD CONSTRAINT adhpaddress_pkey PRIMARY KEY (adhpaddress_pk, validfrom, validto);


--
-- TOC entry 4578 (class 2606 OID 103871)
-- Name: adhpcolloc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adhpcolloc
    ADD CONSTRAINT adhpcolloc_pkey PRIMARY KEY (adhpcolloc_pk, validfrom, validto);


--
-- TOC entry 4581 (class 2606 OID 103890)
-- Name: adhpgndser_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adhpgndser
    ADD CONSTRAINT adhpgndser_pkey PRIMARY KEY (adhpgndser_pk, validfrom, validto);


--
-- TOC entry 4584 (class 2606 OID 103909)
-- Name: adhpgndsvaddr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adhpgndsvaddr
    ADD CONSTRAINT adhpgndsvaddr_pkey PRIMARY KEY (adhpgndsvaddr_pk, validfrom, validto);


--
-- TOC entry 4587 (class 2606 OID 103928)
-- Name: adhpnavaid_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adhpnavaid
    ADD CONSTRAINT adhpnavaid_pkey PRIMARY KEY (adhpnavaid_pk, validfrom, validto);


--
-- TOC entry 4590 (class 2606 OID 103947)
-- Name: adhppsgrfac_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adhppsgrfac
    ADD CONSTRAINT adhppsgrfac_pkey PRIMARY KEY (adhppsgrfac_pk, validfrom, validto);


--
-- TOC entry 4593 (class 2606 OID 103966)
-- Name: adhpuselimit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adhpuselimit
    ADD CONSTRAINT adhpuselimit_pkey PRIMARY KEY (adhpuselimit_pk, validfrom, validto);


--
-- TOC entry 4596 (class 2606 OID 103985)
-- Name: aerogndlgt_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY aerogndlgt
    ADD CONSTRAINT aerogndlgt_pkey PRIMARY KEY (aerogndlgt_pk, validfrom, validto);


--
-- TOC entry 4599 (class 2606 OID 104004)
-- Name: airport_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airport
    ADD CONSTRAINT airport_pkey PRIMARY KEY (airport_pk, validfrom, validto);


--
-- TOC entry 4602 (class 2606 OID 104023)
-- Name: airporthspot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airporthspot
    ADD CONSTRAINT airporthspot_pkey PRIMARY KEY (airporthspot_pk, validfrom, validto);


--
-- TOC entry 4605 (class 2606 OID 104042)
-- Name: airportinasp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airportinasp
    ADD CONSTRAINT airportinasp_pkey PRIMARY KEY (airportinasp_pk, validfrom, validto);


--
-- TOC entry 4608 (class 2606 OID 104061)
-- Name: airspace_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airspace
    ADD CONSTRAINT airspace_pkey PRIMARY KEY (airspace_pk, validfrom, validto);


--
-- TOC entry 4611 (class 2606 OID 104080)
-- Name: airspacebdr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airspacebdr
    ADD CONSTRAINT airspacebdr_pkey PRIMARY KEY (airspacebdr_pk, validfrom, validto);


--
-- TOC entry 4614 (class 2606 OID 104093)
-- Name: airspaceclass_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airspaceclass
    ADD CONSTRAINT airspaceclass_pkey PRIMARY KEY (airspaceclass_pk, validfrom, validto);


--
-- TOC entry 4617 (class 2606 OID 104112)
-- Name: airspacevtx_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airspacevtx
    ADD CONSTRAINT airspacevtx_pkey PRIMARY KEY (airspacevtx_pk, validfrom, validto);


--
-- TOC entry 4620 (class 2606 OID 104131)
-- Name: airspcassoc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airspcassoc
    ADD CONSTRAINT airspcassoc_pkey PRIMARY KEY (airspcassoc_pk, validfrom, validto);


--
-- TOC entry 4623 (class 2606 OID 104144)
-- Name: airspcbdrxng_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airspcbdrxng
    ADD CONSTRAINT airspcbdrxng_pkey PRIMARY KEY (airspcbdrxng_pk, validfrom, validto);


--
-- TOC entry 4626 (class 2606 OID 104163)
-- Name: airway_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airway
    ADD CONSTRAINT airway_pkey PRIMARY KEY (airway_pk, validfrom, validto);


--
-- TOC entry 4629 (class 2606 OID 104182)
-- Name: airwayseg_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY airwayseg
    ADD CONSTRAINT airwayseg_pkey PRIMARY KEY (airwayseg_pk, validfrom, validto);


--
-- TOC entry 4632 (class 2606 OID 104195)
-- Name: altrecdist_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY altrecdist
    ADD CONSTRAINT altrecdist_pkey PRIMARY KEY (altrecdist_pk, validfrom, validto);


--
-- TOC entry 4635 (class 2606 OID 104208)
-- Name: altrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY altrecord
    ADD CONSTRAINT altrecord_pkey PRIMARY KEY (altrecord_pk, validfrom, validto);


--
-- TOC entry 4638 (class 2606 OID 104227)
-- Name: apron_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY apron
    ADD CONSTRAINT apron_pkey PRIMARY KEY (apron_pk, validfrom, validto);


--
-- TOC entry 4641 (class 2606 OID 104240)
-- Name: apronelem_geom_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY apronelem_geom
    ADD CONSTRAINT apronelem_geom_pkey PRIMARY KEY (apronelem_geom_pk, validfrom, validto);


--
-- TOC entry 4644 (class 2606 OID 104259)
-- Name: apronelement_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY apronelement
    ADD CONSTRAINT apronelement_pkey PRIMARY KEY (apronelement_pk, validfrom, validto);


--
-- TOC entry 4647 (class 2606 OID 104278)
-- Name: apronlgtsys_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY apronlgtsys
    ADD CONSTRAINT apronlgtsys_pkey PRIMARY KEY (apronlgtsys_pk, validfrom, validto);


--
-- TOC entry 4650 (class 2606 OID 104297)
-- Name: arrestinggear_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY arrestinggear
    ADD CONSTRAINT arrestinggear_pkey PRIMARY KEY (arrestinggear_pk, validfrom, validto);


--
-- TOC entry 4653 (class 2606 OID 104310)
-- Name: arrstnggrrwydir_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY arrstnggrrwydir
    ADD CONSTRAINT arrstnggrrwydir_pkey PRIMARY KEY (arrstnggrrwydir_pk, validfrom, validto);


--
-- TOC entry 4656 (class 2606 OID 104329)
-- Name: authforasp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authforasp
    ADD CONSTRAINT authforasp_pkey PRIMARY KEY (authforasp_pk, validfrom, validto);


--
-- TOC entry 4659 (class 2606 OID 104348)
-- Name: awyrst_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY awyrst
    ADD CONSTRAINT awyrst_pkey PRIMARY KEY (awyrst_pk, validfrom, validto);


--
-- TOC entry 4662 (class 2606 OID 104361)
-- Name: awyrstalt_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY awyrstalt
    ADD CONSTRAINT awyrstalt_pkey PRIMARY KEY (awyrstalt_pk, validfrom, validto);


--
-- TOC entry 4665 (class 2606 OID 104374)
-- Name: awyrstlnk_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY awyrstlnk
    ADD CONSTRAINT awyrstlnk_pkey PRIMARY KEY (awyrstlnk_pk, validfrom, validto);


--
-- TOC entry 4668 (class 2606 OID 104387)
-- Name: awyrstto_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY awyrstto
    ADD CONSTRAINT awyrstto_pkey PRIMARY KEY (awyrstto_pk, validfrom, validto);


--
-- TOC entry 4671 (class 2606 OID 104406)
-- Name: callsign_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY callsign
    ADD CONSTRAINT callsign_pkey PRIMARY KEY (callsign_pk, validfrom, validto);


--
-- TOC entry 4674 (class 2606 OID 104419)
-- Name: conditioncombo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY conditioncombo
    ADD CONSTRAINT conditioncombo_pkey PRIMARY KEY (conditioncombo_pk, validfrom, validto);


--
-- TOC entry 4677 (class 2606 OID 104432)
-- Name: coroute_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY coroute
    ADD CONSTRAINT coroute_pkey PRIMARY KEY (coroute_pk, validfrom, validto);


--
-- TOC entry 4680 (class 2606 OID 104451)
-- Name: deicingarea_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY deicingarea
    ADD CONSTRAINT deicingarea_pkey PRIMARY KEY (deicingarea_pk, validfrom, validto);


--
-- TOC entry 4683 (class 2606 OID 104464)
-- Name: directflt_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY directflt
    ADD CONSTRAINT directflt_pkey PRIMARY KEY (directflt_pk, validfrom, validto);


--
-- TOC entry 4686 (class 2606 OID 104483)
-- Name: dirfinder_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dirfinder
    ADD CONSTRAINT dirfinder_pkey PRIMARY KEY (dirfinder_pk, validfrom, validto);


--
-- TOC entry 4689 (class 2606 OID 104502)
-- Name: dme_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dme
    ADD CONSTRAINT dme_pkey PRIMARY KEY (dme_pk, validfrom, validto);


--
-- TOC entry 4692 (class 2606 OID 104521)
-- Name: dmelimitation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dmelimitation
    ADD CONSTRAINT dmelimitation_pkey PRIMARY KEY (dmelimitation_pk, validfrom, validto);


--
-- TOC entry 4695 (class 2606 OID 104534)
-- Name: flightclass_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY flightclass
    ADD CONSTRAINT flightclass_pkey PRIMARY KEY (flightclass_pk, validfrom, validto);


--
-- TOC entry 4698 (class 2606 OID 104547)
-- Name: flowcndellvl_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY flowcndellvl
    ADD CONSTRAINT flowcndellvl_pkey PRIMARY KEY (flowcndellvl_pk, validfrom, validto);


--
-- TOC entry 4701 (class 2606 OID 104560)
-- Name: flowcondcomb_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY flowcondcomb
    ADD CONSTRAINT flowcondcomb_pkey PRIMARY KEY (flowcondcomb_pk, validfrom, validto);


--
-- TOC entry 4704 (class 2606 OID 104573)
-- Name: flowcondelem_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY flowcondelem
    ADD CONSTRAINT flowcondelem_pkey PRIMARY KEY (flowcondelem_pk, validfrom, validto);


--
-- TOC entry 4707 (class 2606 OID 104586)
-- Name: fltplnardpfix_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY fltplnardpfix
    ADD CONSTRAINT fltplnardpfix_pkey PRIMARY KEY (fltplnardpfix_pk, validfrom, validto);


--
-- TOC entry 4710 (class 2606 OID 104599)
-- Name: fltplnardptim_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY fltplnardptim
    ADD CONSTRAINT fltplnardptim_pkey PRIMARY KEY (fltplnardptim_pk, validfrom, validto);


--
-- TOC entry 4713 (class 2606 OID 104612)
-- Name: fltplnarrdep_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY fltplnarrdep
    ADD CONSTRAINT fltplnarrdep_pkey PRIMARY KEY (fltplnarrdep_pk, validfrom, validto);


--
-- TOC entry 4716 (class 2606 OID 104631)
-- Name: frequency_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY frequency
    ADD CONSTRAINT frequency_pkey PRIMARY KEY (frequency_pk, validfrom, validto);


--
-- TOC entry 4719 (class 2606 OID 104650)
-- Name: fuel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY fuel
    ADD CONSTRAINT fuel_pkey PRIMARY KEY (fuel_pk, validfrom, validto);


--
-- TOC entry 4722 (class 2606 OID 104669)
-- Name: gatestand_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY gatestand
    ADD CONSTRAINT gatestand_pkey PRIMARY KEY (gatestand_pk, validfrom, validto);


--
-- TOC entry 4725 (class 2606 OID 104688)
-- Name: geoborder_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geoborder
    ADD CONSTRAINT geoborder_pkey PRIMARY KEY (geoborder_pk, validfrom, validto);


--
-- TOC entry 4728 (class 2606 OID 104707)
-- Name: geobordervtx_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geobordervtx
    ADD CONSTRAINT geobordervtx_pkey PRIMARY KEY (geobordervtx_pk, validfrom, validto);


--
-- TOC entry 4731 (class 2606 OID 104720)
-- Name: georeflink_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY georeflink
    ADD CONSTRAINT georeflink_pkey PRIMARY KEY (georeflink_pk, validfrom, validto);


--
-- TOC entry 4734 (class 2606 OID 104733)
-- Name: georeftable_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY georeftable
    ADD CONSTRAINT georeftable_pkey PRIMARY KEY (georeftable_pk, validfrom, validto);


--
-- TOC entry 4737 (class 2606 OID 104746)
-- Name: gls_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY gls
    ADD CONSTRAINT gls_pkey PRIMARY KEY (gls_pk, validfrom, validto);


--
-- TOC entry 4740 (class 2606 OID 104765)
-- Name: gplimitation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY gplimitation
    ADD CONSTRAINT gplimitation_pkey PRIMARY KEY (gplimitation_pk, validfrom, validto);


--
-- TOC entry 4743 (class 2606 OID 104784)
-- Name: gridmora_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY gridmora
    ADD CONSTRAINT gridmora_pkey PRIMARY KEY (gridmora_pk, validfrom, validto);


--
-- TOC entry 4746 (class 2606 OID 104803)
-- Name: guidanceline_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY guidanceline
    ADD CONSTRAINT guidanceline_pkey PRIMARY KEY (guidanceline_pk, validfrom, validto);


--
-- TOC entry 4749 (class 2606 OID 104822)
-- Name: holdingproc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY holdingproc
    ADD CONSTRAINT holdingproc_pkey PRIMARY KEY (holdingproc_pk, validfrom, validto);


--
-- TOC entry 4752 (class 2606 OID 104841)
-- Name: ilsgp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ilsgp
    ADD CONSTRAINT ilsgp_pkey PRIMARY KEY (ilsgp_pk, validfrom, validto);


--
-- TOC entry 4755 (class 2606 OID 104860)
-- Name: ilsllz_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ilsllz
    ADD CONSTRAINT ilsllz_pkey PRIMARY KEY (ilsllz_pk, validfrom, validto);


--
-- TOC entry 4758 (class 2606 OID 104879)
-- Name: lgtelement_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY lgtelement
    ADD CONSTRAINT lgtelement_pkey PRIMARY KEY (lgtelement_pk, validfrom, validto);


--
-- TOC entry 4761 (class 2606 OID 104892)
-- Name: lgtelemstatus_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY lgtelemstatus
    ADD CONSTRAINT lgtelemstatus_pkey PRIMARY KEY (lgtelemstatus_pk, validfrom, validto);


--
-- TOC entry 4764 (class 2606 OID 104911)
-- Name: llzlimitation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY llzlimitation
    ADD CONSTRAINT llzlimitation_pkey PRIMARY KEY (llzlimitation_pk, validfrom, validto);


--
-- TOC entry 4767 (class 2606 OID 104930)
-- Name: marker_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY marker
    ADD CONSTRAINT marker_pkey PRIMARY KEY (marker_pk, validfrom, validto);


--
-- TOC entry 4770 (class 2606 OID 104949)
-- Name: marking_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY marking
    ADD CONSTRAINT marking_pkey PRIMARY KEY (marking_pk, validfrom, validto);


--
-- TOC entry 4773 (class 2606 OID 104968)
-- Name: markingelement_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY markingelement
    ADD CONSTRAINT markingelement_pkey PRIMARY KEY (markingelement_pk, validfrom, validto);


--
-- TOC entry 4776 (class 2606 OID 104981)
-- Name: meteorology_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meteorology
    ADD CONSTRAINT meteorology_pkey PRIMARY KEY (meteorology_pk, validfrom, validto);


--
-- TOC entry 4779 (class 2606 OID 105000)
-- Name: metsvc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY metsvc
    ADD CONSTRAINT metsvc_pkey PRIMARY KEY (metsvc_pk, validfrom, validto);


--
-- TOC entry 4782 (class 2606 OID 105019)
-- Name: mlsazimuth_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY mlsazimuth
    ADD CONSTRAINT mlsazimuth_pkey PRIMARY KEY (mlsazimuth_pk, validfrom, validto);


--
-- TOC entry 4785 (class 2606 OID 105038)
-- Name: mlselevation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY mlselevation
    ADD CONSTRAINT mlselevation_pkey PRIMARY KEY (mlselevation_pk, validfrom, validto);


--
-- TOC entry 4788 (class 2606 OID 105057)
-- Name: msa_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY msa
    ADD CONSTRAINT msa_pkey PRIMARY KEY (msa_pk, validfrom, validto);


--
-- TOC entry 4791 (class 2606 OID 105076)
-- Name: msagroup_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY msagroup
    ADD CONSTRAINT msagroup_pkey PRIMARY KEY (msagroup_pk, validfrom, validto);


--
-- TOC entry 4794 (class 2606 OID 105095)
-- Name: navaids_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY navaids
    ADD CONSTRAINT navaids_pkey PRIMARY KEY (navaids_pk, validfrom, validto);


--
-- TOC entry 4797 (class 2606 OID 105108)
-- Name: navaidscomp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY navaidscomp
    ADD CONSTRAINT navaidscomp_pkey PRIMARY KEY (navaidscomp_pk, validfrom, validto);


--
-- TOC entry 4800 (class 2606 OID 105127)
-- Name: navsysangind_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY navsysangind
    ADD CONSTRAINT navsysangind_pkey PRIMARY KEY (navsysangind_pk, validfrom, validto);


--
-- TOC entry 4803 (class 2606 OID 105146)
-- Name: navsyschkpt_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY navsyschkpt
    ADD CONSTRAINT navsyschkpt_pkey PRIMARY KEY (navsyschkpt_pk, validfrom, validto);


--
-- TOC entry 4806 (class 2606 OID 105165)
-- Name: navsysdistind_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY navsysdistind
    ADD CONSTRAINT navsysdistind_pkey PRIMARY KEY (navsysdistind_pk, validfrom, validto);


--
-- TOC entry 4809 (class 2606 OID 105184)
-- Name: ndb_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ndb
    ADD CONSTRAINT ndb_pkey PRIMARY KEY (ndb_pk, validfrom, validto);


--
-- TOC entry 4812 (class 2606 OID 105203)
-- Name: ndblimitation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ndblimitation
    ADD CONSTRAINT ndblimitation_pkey PRIMARY KEY (ndblimitation_pk, validfrom, validto);


--
-- TOC entry 4815 (class 2606 OID 105222)
-- Name: nitrogen_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY nitrogen
    ADD CONSTRAINT nitrogen_pkey PRIMARY KEY (nitrogen_pk, validfrom, validto);


--
-- TOC entry 4818 (class 2606 OID 105241)
-- Name: ntim_template_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ntim_template
    ADD CONSTRAINT ntim_template_pkey PRIMARY KEY (ntim_template_pk, validfrom, validto);


--
-- TOC entry 4821 (class 2606 OID 105254)
-- Name: nvdtimesheet_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY nvdtimesheet
    ADD CONSTRAINT nvdtimesheet_pkey PRIMARY KEY (nvdtimesheet_pk, validfrom, validto);


--
-- TOC entry 4824 (class 2606 OID 105273)
-- Name: obsarea_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY obsarea
    ADD CONSTRAINT obsarea_pkey PRIMARY KEY (obsarea_pk, validfrom, validto);


--
-- TOC entry 4827 (class 2606 OID 105292)
-- Name: obsarealink_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY obsarealink
    ADD CONSTRAINT obsarealink_pkey PRIMARY KEY (obsarealink_pk, validfrom, validto);


--
-- TOC entry 4830 (class 2606 OID 105311)
-- Name: obsassessarea_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY obsassessarea
    ADD CONSTRAINT obsassessarea_pkey PRIMARY KEY (obsassessarea_pk, validfrom, validto);


--
-- TOC entry 4833 (class 2606 OID 105324)
-- Name: obspart_geom_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY obspart_geom
    ADD CONSTRAINT obspart_geom_pkey PRIMARY KEY (obspart_geom_pk, validfrom, validto);


--
-- TOC entry 4836 (class 2606 OID 105343)
-- Name: obstacle_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY obstacle
    ADD CONSTRAINT obstacle_pkey PRIMARY KEY (obstacle_pk, validfrom, validto);


--
-- TOC entry 4839 (class 2606 OID 105362)
-- Name: obstacleinssa_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY obstacleinssa
    ADD CONSTRAINT obstacleinssa_pkey PRIMARY KEY (obstacleinssa_pk, validfrom, validto);


--
-- TOC entry 4842 (class 2606 OID 105381)
-- Name: obstaclepart_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY obstaclepart
    ADD CONSTRAINT obstaclepart_pkey PRIMARY KEY (obstaclepart_pk, validfrom, validto);


--
-- TOC entry 4845 (class 2606 OID 105400)
-- Name: ocaoch_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ocaoch
    ADD CONSTRAINT ocaoch_pkey PRIMARY KEY (ocaoch_pk, validfrom, validto);


--
-- TOC entry 4848 (class 2606 OID 105413)
-- Name: ocaochapptyp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ocaochapptyp
    ADD CONSTRAINT ocaochapptyp_pkey PRIMARY KEY (ocaochapptyp_pk, validfrom, validto);


--
-- TOC entry 4851 (class 2606 OID 105432)
-- Name: oil_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY oil
    ADD CONSTRAINT oil_pkey PRIMARY KEY (oil_pk, validfrom, validto);


--
-- TOC entry 4854 (class 2606 OID 105451)
-- Name: orgauth_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgauth
    ADD CONSTRAINT orgauth_pkey PRIMARY KEY (orgauth_pk, validfrom, validto);


--
-- TOC entry 4857 (class 2606 OID 105470)
-- Name: orgauthaddr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgauthaddr
    ADD CONSTRAINT orgauthaddr_pkey PRIMARY KEY (orgauthaddr_pk, validfrom, validto);


--
-- TOC entry 4860 (class 2606 OID 105489)
-- Name: orgauthassoc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orgauthassoc
    ADD CONSTRAINT orgauthassoc_pkey PRIMARY KEY (orgauthassoc_pk, validfrom, validto);


--
-- TOC entry 4863 (class 2606 OID 105508)
-- Name: oxygen_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY oxygen
    ADD CONSTRAINT oxygen_pkey PRIMARY KEY (oxygen_pk, validfrom, validto);


--
-- TOC entry 4866 (class 2606 OID 105521)
-- Name: pdflvl_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pdflvl
    ADD CONSTRAINT pdflvl_pkey PRIMARY KEY (pdflvl_pk, validfrom, validto);


--
-- TOC entry 4869 (class 2606 OID 105540)
-- Name: pdflvlcol_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pdflvlcol
    ADD CONSTRAINT pdflvlcol_pkey PRIMARY KEY (pdflvlcol_pk, validfrom, validto);


--
-- TOC entry 4872 (class 2606 OID 105560)
-- Name: pdflvltbl_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pdflvltbl
    ADD CONSTRAINT pdflvltbl_pkey PRIMARY KEY (pdflvltbl_pk, validfrom, validto);


--
-- TOC entry 5144 (class 2606 OID 112126)
-- Name: primary_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY boundingbox
    ADD CONSTRAINT primary_key PRIMARY KEY (id);


--
-- TOC entry 4875 (class 2606 OID 105579)
-- Name: proctransiction_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY proctransiction
    ADD CONSTRAINT proctransiction_pkey PRIMARY KEY (proctransiction_pk, validfrom, validto);


--
-- TOC entry 4878 (class 2606 OID 105592)
-- Name: proctransleg_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY proctransleg
    ADD CONSTRAINT proctransleg_pkey PRIMARY KEY (proctransleg_pk, validfrom, validto);


--
-- TOC entry 4881 (class 2606 OID 105611)
-- Name: prroute_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY prroute
    ADD CONSTRAINT prroute_pkey PRIMARY KEY (prroute_pk, validfrom, validto);


--
-- TOC entry 4884 (class 2606 OID 105624)
-- Name: prroutetime_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY prroutetime
    ADD CONSTRAINT prroutetime_pkey PRIMARY KEY (prroutetime_pk, validfrom, validto);


--
-- TOC entry 4887 (class 2606 OID 105637)
-- Name: radarcomponent_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY radarcomponent
    ADD CONSTRAINT radarcomponent_pkey PRIMARY KEY (radarcomponent_pk, validfrom, validto);


--
-- TOC entry 4890 (class 2606 OID 105656)
-- Name: radarequipment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY radarequipment
    ADD CONSTRAINT radarequipment_pkey PRIMARY KEY (radarequipment_pk, validfrom, validto);


--
-- TOC entry 4893 (class 2606 OID 105675)
-- Name: radarsysrwylink_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY radarsysrwylink
    ADD CONSTRAINT radarsysrwylink_pkey PRIMARY KEY (radarsysrwylink_pk, validfrom, validto);


--
-- TOC entry 4896 (class 2606 OID 105694)
-- Name: radarsystem_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY radarsystem
    ADD CONSTRAINT radarsystem_pkey PRIMARY KEY (radarsystem_pk, validfrom, validto);


--
-- TOC entry 4899 (class 2606 OID 105713)
-- Name: radiosonde_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY radiosonde
    ADD CONSTRAINT radiosonde_pkey PRIMARY KEY (radiosonde_pk, validfrom, validto);


--
-- TOC entry 4902 (class 2606 OID 105732)
-- Name: reflector_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reflector
    ADD CONSTRAINT reflector_pkey PRIMARY KEY (reflector_pk, validfrom, validto);


--
-- TOC entry 4905 (class 2606 OID 105751)
-- Name: road_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY road
    ADD CONSTRAINT road_pkey PRIMARY KEY (road_pk, validfrom, validto);


--
-- TOC entry 4908 (class 2606 OID 105764)
-- Name: rteportion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rteportion
    ADD CONSTRAINT rteportion_pkey PRIMARY KEY (rteportion_pk, validfrom, validto);


--
-- TOC entry 4911 (class 2606 OID 105783)
-- Name: rteseguse_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rteseguse
    ADD CONSTRAINT rteseguse_pkey PRIMARY KEY (rteseguse_pk, validfrom, validto);


--
-- TOC entry 4914 (class 2606 OID 105796)
-- Name: rteseguselvl_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rteseguselvl
    ADD CONSTRAINT rteseguselvl_pkey PRIMARY KEY (rteseguselvl_pk, validfrom, validto);


--
-- TOC entry 4917 (class 2606 OID 105815)
-- Name: runway_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY runway
    ADD CONSTRAINT runway_pkey PRIMARY KEY (runway_pk, validfrom, validto);


--
-- TOC entry 4920 (class 2606 OID 105834)
-- Name: rwdals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwdals
    ADD CONSTRAINT rwdals_pkey PRIMARY KEY (rwdals_pk, validfrom, validto);


--
-- TOC entry 4923 (class 2606 OID 105853)
-- Name: rwddecdist_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwddecdist
    ADD CONSTRAINT rwddecdist_pkey PRIMARY KEY (rwddecdist_pk, validfrom, validto);


--
-- TOC entry 4926 (class 2606 OID 105872)
-- Name: rwdlgtgrp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwdlgtgrp
    ADD CONSTRAINT rwdlgtgrp_pkey PRIMARY KEY (rwdlgtgrp_pk, validfrom, validto);


--
-- TOC entry 4929 (class 2606 OID 105891)
-- Name: rwdlgtsys_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwdlgtsys
    ADD CONSTRAINT rwdlgtsys_pkey PRIMARY KEY (rwdlgtsys_pk, validfrom, validto);


--
-- TOC entry 4935 (class 2606 OID 105923)
-- Name: rwdprtarea_geom_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwdprtarea_geom
    ADD CONSTRAINT rwdprtarea_geom_pkey PRIMARY KEY (rwdprtarea_geom_pk, validfrom, validto);


--
-- TOC entry 4932 (class 2606 OID 105910)
-- Name: rwdprtarea_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwdprtarea
    ADD CONSTRAINT rwdprtarea_pkey PRIMARY KEY (rwdprtarea_pk, validfrom, validto);


--
-- TOC entry 4938 (class 2606 OID 105942)
-- Name: rwyblastpad_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwyblastpad
    ADD CONSTRAINT rwyblastpad_pkey PRIMARY KEY (rwyblastpad_pk, validfrom, validto);


--
-- TOC entry 4941 (class 2606 OID 105961)
-- Name: rwyclinept_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwyclinept
    ADD CONSTRAINT rwyclinept_pkey PRIMARY KEY (rwyclinept_pk, validfrom, validto);


--
-- TOC entry 4944 (class 2606 OID 105980)
-- Name: rwydirection_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwydirection
    ADD CONSTRAINT rwydirection_pkey PRIMARY KEY (rwydirection_pk, validfrom, validto);


--
-- TOC entry 4947 (class 2606 OID 105999)
-- Name: rwydirectnstr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwydirectnstr
    ADD CONSTRAINT rwydirectnstr_pkey PRIMARY KEY (rwydirectnstr_pk, validfrom, validto);


--
-- TOC entry 4950 (class 2606 OID 106018)
-- Name: rwyelement_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rwyelement
    ADD CONSTRAINT rwyelement_pkey PRIMARY KEY (rwyelement_pk, validfrom, validto);


--
-- TOC entry 4953 (class 2606 OID 106037)
-- Name: sdf_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sdf
    ADD CONSTRAINT sdf_pkey PRIMARY KEY (sdf_pk, validfrom, validto);


--
-- TOC entry 4956 (class 2606 OID 106056)
-- Name: segmnt_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY segmnt
    ADD CONSTRAINT segmnt_pkey PRIMARY KEY (segmnt_pk, validfrom, validto);


--
-- TOC entry 4959 (class 2606 OID 106075)
-- Name: service_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY service
    ADD CONSTRAINT service_pkey PRIMARY KEY (service_pk, validfrom, validto);


--
-- TOC entry 4962 (class 2606 OID 106094)
-- Name: sigpointinas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY sigpointinas
    ADD CONSTRAINT sigpointinas_pkey PRIMARY KEY (sigpointinas_pk, validfrom, validto);


--
-- TOC entry 4965 (class 2606 OID 106113)
-- Name: specialdate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY specialdate
    ADD CONSTRAINT specialdate_pkey PRIMARY KEY (specialdate_pk, validfrom, validto);


--
-- TOC entry 4968 (class 2606 OID 106132)
-- Name: specnavstn_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY specnavstn
    ADD CONSTRAINT specnavstn_pkey PRIMARY KEY (specnavstn_pk, validfrom, validto);


--
-- TOC entry 4971 (class 2606 OID 106151)
-- Name: specnavsys_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY specnavsys
    ADD CONSTRAINT specnavsys_pkey PRIMARY KEY (specnavsys_pk, validfrom, validto);


--
-- TOC entry 4974 (class 2606 OID 106170)
-- Name: ssa_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ssa
    ADD CONSTRAINT ssa_pkey PRIMARY KEY (ssa_pk, validfrom, validto);


--
-- TOC entry 4977 (class 2606 OID 106183)
-- Name: ssapath_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ssapath
    ADD CONSTRAINT ssapath_pkey PRIMARY KEY (ssapath_pk, validfrom, validto);


--
-- TOC entry 4980 (class 2606 OID 106202)
-- Name: ssapathleg_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ssapathleg
    ADD CONSTRAINT ssapathleg_pkey PRIMARY KEY (ssapathleg_pk, validfrom, validto);


--
-- TOC entry 4983 (class 2606 OID 106215)
-- Name: ssapathlegarea_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ssapathlegarea
    ADD CONSTRAINT ssapathlegarea_pkey PRIMARY KEY (ssapathlegarea_pk, validfrom, validto);


--
-- TOC entry 4986 (class 2606 OID 106234)
-- Name: ssaseg_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ssaseg
    ADD CONSTRAINT ssaseg_pkey PRIMARY KEY (ssaseg_pk, validfrom, validto);


--
-- TOC entry 4989 (class 2606 OID 106253)
-- Name: ssausage_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ssausage
    ADD CONSTRAINT ssausage_pkey PRIMARY KEY (ssausage_pk, validfrom, validto);


--
-- TOC entry 4992 (class 2606 OID 106272)
-- Name: surveyctrlpt_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY surveyctrlpt
    ADD CONSTRAINT surveyctrlpt_pkey PRIMARY KEY (surveyctrlpt_pk, validfrom, validto);


--
-- TOC entry 4995 (class 2606 OID 106291)
-- Name: svcinasp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY svcinasp
    ADD CONSTRAINT svcinasp_pkey PRIMARY KEY (svcinasp_pk, validfrom, validto);


--
-- TOC entry 4998 (class 2606 OID 106310)
-- Name: svconadhp_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY svconadhp
    ADD CONSTRAINT svconadhp_pkey PRIMARY KEY (svconadhp_pk, validfrom, validto);


--
-- TOC entry 5001 (class 2606 OID 106329)
-- Name: svconhldgproc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY svconhldgproc
    ADD CONSTRAINT svconhldgproc_pkey PRIMARY KEY (svconhldgproc_pk, validfrom, validto);


--
-- TOC entry 5004 (class 2606 OID 106348)
-- Name: svconrteseg_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY svconrteseg
    ADD CONSTRAINT svconrteseg_pkey PRIMARY KEY (svconrteseg_pk, validfrom, validto);


--
-- TOC entry 5007 (class 2606 OID 106367)
-- Name: svconssa_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY svconssa
    ADD CONSTRAINT svconssa_pkey PRIMARY KEY (svconssa_pk, validfrom, validto);


--
-- TOC entry 5010 (class 2606 OID 106386)
-- Name: tacan_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tacan
    ADD CONSTRAINT tacan_pkey PRIMARY KEY (tacan_pk, validfrom, validto);


--
-- TOC entry 5013 (class 2606 OID 106405)
-- Name: tacanlimitatn_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tacanlimitatn
    ADD CONSTRAINT tacanlimitatn_pkey PRIMARY KEY (tacanlimitatn_pk, validfrom, validto);


--
-- TOC entry 5016 (class 2606 OID 106424)
-- Name: tfcflowrestr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tfcflowrestr
    ADD CONSTRAINT tfcflowrestr_pkey PRIMARY KEY (tfcflowrestr_pk, validfrom, validto);


--
-- TOC entry 5019 (class 2606 OID 106437)
-- Name: tfcflowrte_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tfcflowrte
    ADD CONSTRAINT tfcflowrte_pkey PRIMARY KEY (tfcflowrte_pk, validfrom, validto);


--
-- TOC entry 5022 (class 2606 OID 106450)
-- Name: tfcflwrtelem_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tfcflwrtelem
    ADD CONSTRAINT tfcflwrtelem_pkey PRIMARY KEY (tfcflwrtelem_pk, validfrom, validto);


--
-- TOC entry 5025 (class 2606 OID 106463)
-- Name: tfcflwrtellv_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tfcflwrtellv
    ADD CONSTRAINT tfcflwrtellv_pkey PRIMARY KEY (tfcflwrtellv_pk, validfrom, validto);


--
-- TOC entry 5028 (class 2606 OID 106482)
-- Name: time_template_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY time_template
    ADD CONSTRAINT time_template_pkey PRIMARY KEY (time_template_pk, validfrom, validto);


--
-- TOC entry 5031 (class 2606 OID 106501)
-- Name: timesheet_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY timesheet
    ADD CONSTRAINT timesheet_pkey PRIMARY KEY (timesheet_pk, validfrom, validto);


--
-- TOC entry 5037 (class 2606 OID 106533)
-- Name: tlof_geom_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tlof_geom
    ADD CONSTRAINT tlof_geom_pkey PRIMARY KEY (tlof_geom_pk, validfrom, validto);


--
-- TOC entry 5034 (class 2606 OID 106520)
-- Name: tlof_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tlof
    ADD CONSTRAINT tlof_pkey PRIMARY KEY (tlof_pk, validfrom, validto);


--
-- TOC entry 5040 (class 2606 OID 106552)
-- Name: tloflgtsys_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tloflgtsys
    ADD CONSTRAINT tloflgtsys_pkey PRIMARY KEY (tloflgtsys_pk, validfrom, validto);


--
-- TOC entry 5043 (class 2606 OID 106571)
-- Name: tlofsafearea_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tlofsafearea
    ADD CONSTRAINT tlofsafearea_pkey PRIMARY KEY (tlofsafearea_pk, validfrom, validto);


--
-- TOC entry 5046 (class 2606 OID 106584)
-- Name: tlofsarea_geom_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tlofsarea_geom
    ADD CONSTRAINT tlofsarea_geom_pkey PRIMARY KEY (tlofsarea_geom_pk, validfrom, validto);


--
-- TOC entry 5049 (class 2606 OID 106603)
-- Name: twy_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY twy
    ADD CONSTRAINT twy_pkey PRIMARY KEY (twy_pk, validfrom, validto);


--
-- TOC entry 5052 (class 2606 OID 106622)
-- Name: twyelement_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY twyelement
    ADD CONSTRAINT twyelement_pkey PRIMARY KEY (twyelement_pk, validfrom, validto);


--
-- TOC entry 5055 (class 2606 OID 106641)
-- Name: twyhldpos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY twyhldpos
    ADD CONSTRAINT twyhldpos_pkey PRIMARY KEY (twyhldpos_pk, validfrom, validto);


--
-- TOC entry 5058 (class 2606 OID 106660)
-- Name: twyintrsectn_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY twyintrsectn
    ADD CONSTRAINT twyintrsectn_pkey PRIMARY KEY (twyintrsectn_pk, validfrom, validto);


--
-- TOC entry 5061 (class 2606 OID 106679)
-- Name: twylgtsys_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY twylgtsys
    ADD CONSTRAINT twylgtsys_pkey PRIMARY KEY (twylgtsys_pk, validfrom, validto);


--
-- TOC entry 5064 (class 2606 OID 106698)
-- Name: unit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unit_pk, validfrom, validto);


--
-- TOC entry 5067 (class 2606 OID 106717)
-- Name: unitaddress_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY unitaddress
    ADD CONSTRAINT unitaddress_pkey PRIMARY KEY (unitaddress_pk, validfrom, validto);


--
-- TOC entry 5070 (class 2606 OID 106736)
-- Name: unitassoc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY unitassoc
    ADD CONSTRAINT unitassoc_pkey PRIMARY KEY (unitassoc_pk, validfrom, validto);


--
-- TOC entry 5073 (class 2606 OID 106755)
-- Name: vor_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY vor
    ADD CONSTRAINT vor_pkey PRIMARY KEY (vor_pk, validfrom, validto);


--
-- TOC entry 5076 (class 2606 OID 106774)
-- Name: vorlimitation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY vorlimitation
    ADD CONSTRAINT vorlimitation_pkey PRIMARY KEY (vorlimitation_pk, validfrom, validto);


--
-- TOC entry 5079 (class 2606 OID 106793)
-- Name: waypoint_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY waypoint
    ADD CONSTRAINT waypoint_pkey PRIMARY KEY (waypoint_pk, validfrom, validto);


--
-- TOC entry 5082 (class 2606 OID 106812)
-- Name: workarea_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY workarea
    ADD CONSTRAINT workarea_pkey PRIMARY KEY (workarea_pk, validfrom, validto);


--
-- TOC entry 4570 (class 1259 OID 103834)
-- Name: acftclass_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX acftclass_idx ON acftclass USING btree (acftclass_pk, validfrom, validto);


--
-- TOC entry 4573 (class 1259 OID 103853)
-- Name: adhpaddress_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX adhpaddress_idx ON adhpaddress USING btree (adhpaddress_pk, validfrom, validto);


--
-- TOC entry 4576 (class 1259 OID 103872)
-- Name: adhpcolloc_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX adhpcolloc_idx ON adhpcolloc USING btree (adhpcolloc_pk, validfrom, validto);


--
-- TOC entry 4579 (class 1259 OID 103891)
-- Name: adhpgndser_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX adhpgndser_idx ON adhpgndser USING btree (adhpgndser_pk, validfrom, validto);


--
-- TOC entry 4582 (class 1259 OID 103910)
-- Name: adhpgndsvaddr_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX adhpgndsvaddr_idx ON adhpgndsvaddr USING btree (adhpgndsvaddr_pk, validfrom, validto);


--
-- TOC entry 4585 (class 1259 OID 103929)
-- Name: adhpnavaid_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX adhpnavaid_idx ON adhpnavaid USING btree (adhpnavaid_pk, validfrom, validto);


--
-- TOC entry 4588 (class 1259 OID 103948)
-- Name: adhppsgrfac_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX adhppsgrfac_idx ON adhppsgrfac USING btree (adhppsgrfac_pk, validfrom, validto);


--
-- TOC entry 4591 (class 1259 OID 103967)
-- Name: adhpuselimit_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX adhpuselimit_idx ON adhpuselimit USING btree (adhpuselimit_pk, validfrom, validto);


--
-- TOC entry 4594 (class 1259 OID 103986)
-- Name: aerogndlgt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX aerogndlgt_idx ON aerogndlgt USING btree (aerogndlgt_pk, validfrom, validto);


--
-- TOC entry 4597 (class 1259 OID 104005)
-- Name: airport_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airport_idx ON airport USING btree (airport_pk, validfrom, validto);


--
-- TOC entry 4600 (class 1259 OID 104024)
-- Name: airporthspot_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airporthspot_idx ON airporthspot USING btree (airporthspot_pk, validfrom, validto);


--
-- TOC entry 4603 (class 1259 OID 104043)
-- Name: airportinasp_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airportinasp_idx ON airportinasp USING btree (airportinasp_pk, validfrom, validto);


--
-- TOC entry 4606 (class 1259 OID 104062)
-- Name: airspace_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airspace_idx ON airspace USING btree (airspace_pk, validfrom, validto);

-- created by dpanech
CREATE INDEX airspace_typ_idx ON airspace (typ);

--
-- TOC entry 4609 (class 1259 OID 104081)
-- Name: airspacebdr_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airspacebdr_idx ON airspacebdr USING btree (airspacebdr_pk, validfrom, validto);


--
-- TOC entry 4612 (class 1259 OID 104094)
-- Name: airspaceclass_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airspaceclass_idx ON airspaceclass USING btree (airspaceclass_pk, validfrom, validto);


--
-- TOC entry 4615 (class 1259 OID 104113)
-- Name: airspacevtx_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airspacevtx_idx ON airspacevtx USING btree (airspacevtx_pk, validfrom, validto);

--
-- TOC entry 4618 (class 1259 OID 104132)
-- Name: airspcassoc_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airspcassoc_idx ON airspcassoc USING btree (airspcassoc_pk, validfrom, validto);


--
-- TOC entry 4621 (class 1259 OID 104145)
-- Name: airspcbdrxng_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airspcbdrxng_idx ON airspcbdrxng USING btree (airspcbdrxng_pk, validfrom, validto);


--
-- TOC entry 4624 (class 1259 OID 104164)
-- Name: airway_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airway_idx ON airway USING btree (airway_pk, validfrom, validto);


--
-- TOC entry 4627 (class 1259 OID 104183)
-- Name: airwayseg_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX airwayseg_idx ON airwayseg USING btree (airwayseg_pk, validfrom, validto);


--
-- TOC entry 4630 (class 1259 OID 104196)
-- Name: altrecdist_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX altrecdist_idx ON altrecdist USING btree (altrecdist_pk, validfrom, validto);


--
-- TOC entry 4633 (class 1259 OID 104209)
-- Name: altrecord_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX altrecord_idx ON altrecord USING btree (altrecord_pk, validfrom, validto);


--
-- TOC entry 4636 (class 1259 OID 104228)
-- Name: apron_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX apron_idx ON apron USING btree (apron_pk, validfrom, validto);


--
-- TOC entry 4639 (class 1259 OID 104241)
-- Name: apronelem_geom_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX apronelem_geom_idx ON apronelem_geom USING btree (apronelem_geom_pk, validfrom, validto);


--
-- TOC entry 4642 (class 1259 OID 104260)
-- Name: apronelement_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX apronelement_idx ON apronelement USING btree (apronelement_pk, validfrom, validto);


--
-- TOC entry 4645 (class 1259 OID 104279)
-- Name: apronlgtsys_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX apronlgtsys_idx ON apronlgtsys USING btree (apronlgtsys_pk, validfrom, validto);


--
-- TOC entry 4648 (class 1259 OID 104298)
-- Name: arrestinggear_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX arrestinggear_idx ON arrestinggear USING btree (arrestinggear_pk, validfrom, validto);


--
-- TOC entry 4651 (class 1259 OID 104311)
-- Name: arrstnggrrwydir_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX arrstnggrrwydir_idx ON arrstnggrrwydir USING btree (arrstnggrrwydir_pk, validfrom, validto);


--
-- TOC entry 4654 (class 1259 OID 104330)
-- Name: authforasp_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX authforasp_idx ON authforasp USING btree (authforasp_pk, validfrom, validto);


--
-- TOC entry 4657 (class 1259 OID 104349)
-- Name: awyrst_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX awyrst_idx ON awyrst USING btree (awyrst_pk, validfrom, validto);


--
-- TOC entry 4660 (class 1259 OID 104362)
-- Name: awyrstalt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX awyrstalt_idx ON awyrstalt USING btree (awyrstalt_pk, validfrom, validto);


--
-- TOC entry 4663 (class 1259 OID 104375)
-- Name: awyrstlnk_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX awyrstlnk_idx ON awyrstlnk USING btree (awyrstlnk_pk, validfrom, validto);


--
-- TOC entry 4666 (class 1259 OID 104388)
-- Name: awyrstto_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX awyrstto_idx ON awyrstto USING btree (awyrstto_pk, validfrom, validto);


--
-- TOC entry 4669 (class 1259 OID 104407)
-- Name: callsign_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX callsign_idx ON callsign USING btree (callsign_pk, validfrom, validto);


--
-- TOC entry 4672 (class 1259 OID 104420)
-- Name: conditioncombo_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX conditioncombo_idx ON conditioncombo USING btree (conditioncombo_pk, validfrom, validto);


--
-- TOC entry 4675 (class 1259 OID 104433)
-- Name: coroute_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX coroute_idx ON coroute USING btree (coroute_pk, validfrom, validto);


--
-- TOC entry 4678 (class 1259 OID 104452)
-- Name: deicingarea_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX deicingarea_idx ON deicingarea USING btree (deicingarea_pk, validfrom, validto);


--
-- TOC entry 4681 (class 1259 OID 104465)
-- Name: directflt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX directflt_idx ON directflt USING btree (directflt_pk, validfrom, validto);


--
-- TOC entry 4684 (class 1259 OID 104484)
-- Name: dirfinder_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX dirfinder_idx ON dirfinder USING btree (dirfinder_pk, validfrom, validto);


--
-- TOC entry 4687 (class 1259 OID 104503)
-- Name: dme_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX dme_idx ON dme USING btree (dme_pk, validfrom, validto);


--
-- TOC entry 4690 (class 1259 OID 104522)
-- Name: dmelimitation_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX dmelimitation_idx ON dmelimitation USING btree (dmelimitation_pk, validfrom, validto);


--
-- TOC entry 4693 (class 1259 OID 104535)
-- Name: flightclass_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flightclass_idx ON flightclass USING btree (flightclass_pk, validfrom, validto);


--
-- TOC entry 4696 (class 1259 OID 104548)
-- Name: flowcndellvl_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flowcndellvl_idx ON flowcndellvl USING btree (flowcndellvl_pk, validfrom, validto);


--
-- TOC entry 4699 (class 1259 OID 104561)
-- Name: flowcondcomb_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flowcondcomb_idx ON flowcondcomb USING btree (flowcondcomb_pk, validfrom, validto);


--
-- TOC entry 4702 (class 1259 OID 104574)
-- Name: flowcondelem_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flowcondelem_idx ON flowcondelem USING btree (flowcondelem_pk, validfrom, validto);


--
-- TOC entry 4705 (class 1259 OID 104587)
-- Name: fltplnardpfix_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fltplnardpfix_idx ON fltplnardpfix USING btree (fltplnardpfix_pk, validfrom, validto);


--
-- TOC entry 4708 (class 1259 OID 104600)
-- Name: fltplnardptim_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fltplnardptim_idx ON fltplnardptim USING btree (fltplnardptim_pk, validfrom, validto);


--
-- TOC entry 4711 (class 1259 OID 104613)
-- Name: fltplnarrdep_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fltplnarrdep_idx ON fltplnarrdep USING btree (fltplnarrdep_pk, validfrom, validto);


--
-- TOC entry 4714 (class 1259 OID 104632)
-- Name: frequency_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX frequency_idx ON frequency USING btree (frequency_pk, validfrom, validto);


--
-- TOC entry 4717 (class 1259 OID 104651)
-- Name: fuel_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX fuel_idx ON fuel USING btree (fuel_pk, validfrom, validto);


--
-- TOC entry 4720 (class 1259 OID 104670)
-- Name: gatestand_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX gatestand_idx ON gatestand USING btree (gatestand_pk, validfrom, validto);


--
-- TOC entry 4723 (class 1259 OID 104689)
-- Name: geoborder_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX geoborder_idx ON geoborder USING btree (geoborder_pk, validfrom, validto);


--
-- TOC entry 4726 (class 1259 OID 104708)
-- Name: geobordervtx_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX geobordervtx_idx ON geobordervtx USING btree (geobordervtx_pk, validfrom, validto);


--
-- TOC entry 4729 (class 1259 OID 104721)
-- Name: georeflink_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX georeflink_idx ON georeflink USING btree (georeflink_pk, validfrom, validto);


--
-- TOC entry 4732 (class 1259 OID 104734)
-- Name: georeftable_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX georeftable_idx ON georeftable USING btree (georeftable_pk, validfrom, validto);


--
-- TOC entry 4735 (class 1259 OID 104747)
-- Name: gls_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX gls_idx ON gls USING btree (gls_pk, validfrom, validto);


--
-- TOC entry 4738 (class 1259 OID 104766)
-- Name: gplimitation_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX gplimitation_idx ON gplimitation USING btree (gplimitation_pk, validfrom, validto);


--
-- TOC entry 4741 (class 1259 OID 104785)
-- Name: gridmora_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX gridmora_idx ON gridmora USING btree (gridmora_pk, validfrom, validto);


--
-- TOC entry 4744 (class 1259 OID 104804)
-- Name: guidanceline_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX guidanceline_idx ON guidanceline USING btree (guidanceline_pk, validfrom, validto);


--
-- TOC entry 4747 (class 1259 OID 104823)
-- Name: holdingproc_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX holdingproc_idx ON holdingproc USING btree (holdingproc_pk, validfrom, validto);


--
-- TOC entry 4750 (class 1259 OID 104842)
-- Name: ilsgp_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ilsgp_idx ON ilsgp USING btree (ilsgp_pk, validfrom, validto);


--
-- TOC entry 4753 (class 1259 OID 104861)
-- Name: ilsllz_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ilsllz_idx ON ilsllz USING btree (ilsllz_pk, validfrom, validto);


--
-- TOC entry 4756 (class 1259 OID 104880)
-- Name: lgtelement_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX lgtelement_idx ON lgtelement USING btree (lgtelement_pk, validfrom, validto);


--
-- TOC entry 4759 (class 1259 OID 104893)
-- Name: lgtelemstatus_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX lgtelemstatus_idx ON lgtelemstatus USING btree (lgtelemstatus_pk, validfrom, validto);


--
-- TOC entry 4762 (class 1259 OID 104912)
-- Name: llzlimitation_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX llzlimitation_idx ON llzlimitation USING btree (llzlimitation_pk, validfrom, validto);


--
-- TOC entry 4765 (class 1259 OID 104931)
-- Name: marker_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX marker_idx ON marker USING btree (marker_pk, validfrom, validto);


--
-- TOC entry 4768 (class 1259 OID 104950)
-- Name: marking_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX marking_idx ON marking USING btree (marking_pk, validfrom, validto);


--
-- TOC entry 4771 (class 1259 OID 104969)
-- Name: markingelement_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX markingelement_idx ON markingelement USING btree (markingelement_pk, validfrom, validto);


--
-- TOC entry 4774 (class 1259 OID 104982)
-- Name: meteorology_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX meteorology_idx ON meteorology USING btree (meteorology_pk, validfrom, validto);


--
-- TOC entry 4777 (class 1259 OID 105001)
-- Name: metsvc_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX metsvc_idx ON metsvc USING btree (metsvc_pk, validfrom, validto);


--
-- TOC entry 4780 (class 1259 OID 105020)
-- Name: mlsazimuth_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX mlsazimuth_idx ON mlsazimuth USING btree (mlsazimuth_pk, validfrom, validto);


--
-- TOC entry 4783 (class 1259 OID 105039)
-- Name: mlselevation_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX mlselevation_idx ON mlselevation USING btree (mlselevation_pk, validfrom, validto);


--
-- TOC entry 4786 (class 1259 OID 105058)
-- Name: msa_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX msa_idx ON msa USING btree (msa_pk, validfrom, validto);


--
-- TOC entry 4789 (class 1259 OID 105077)
-- Name: msagroup_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX msagroup_idx ON msagroup USING btree (msagroup_pk, validfrom, validto);


--
-- TOC entry 4792 (class 1259 OID 105096)
-- Name: navaids_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX navaids_idx ON navaids USING btree (navaids_pk, validfrom, validto);


--
-- TOC entry 4795 (class 1259 OID 105109)
-- Name: navaidscomp_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX navaidscomp_idx ON navaidscomp USING btree (navaidscomp_pk, validfrom, validto);


--
-- TOC entry 4798 (class 1259 OID 105128)
-- Name: navsysangind_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX navsysangind_idx ON navsysangind USING btree (navsysangind_pk, validfrom, validto);


--
-- TOC entry 4801 (class 1259 OID 105147)
-- Name: navsyschkpt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX navsyschkpt_idx ON navsyschkpt USING btree (navsyschkpt_pk, validfrom, validto);


--
-- TOC entry 4804 (class 1259 OID 105166)
-- Name: navsysdistind_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX navsysdistind_idx ON navsysdistind USING btree (navsysdistind_pk, validfrom, validto);


--
-- TOC entry 4807 (class 1259 OID 105185)
-- Name: ndb_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ndb_idx ON ndb USING btree (ndb_pk, validfrom, validto);


--
-- TOC entry 4810 (class 1259 OID 105204)
-- Name: ndblimitation_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ndblimitation_idx ON ndblimitation USING btree (ndblimitation_pk, validfrom, validto);


--
-- TOC entry 4813 (class 1259 OID 105223)
-- Name: nitrogen_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX nitrogen_idx ON nitrogen USING btree (nitrogen_pk, validfrom, validto);


--
-- TOC entry 4816 (class 1259 OID 105242)
-- Name: ntim_template_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ntim_template_idx ON ntim_template USING btree (ntim_template_pk, validfrom, validto);


--
-- TOC entry 4819 (class 1259 OID 105255)
-- Name: nvdtimesheet_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX nvdtimesheet_idx ON nvdtimesheet USING btree (nvdtimesheet_pk, validfrom, validto);


--
-- TOC entry 4822 (class 1259 OID 105274)
-- Name: obsarea_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX obsarea_idx ON obsarea USING btree (obsarea_pk, validfrom, validto);


--
-- TOC entry 4825 (class 1259 OID 105293)
-- Name: obsarealink_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX obsarealink_idx ON obsarealink USING btree (obsarealink_pk, validfrom, validto);


--
-- TOC entry 4828 (class 1259 OID 105312)
-- Name: obsassessarea_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX obsassessarea_idx ON obsassessarea USING btree (obsassessarea_pk, validfrom, validto);


--
-- TOC entry 4831 (class 1259 OID 105325)
-- Name: obspart_geom_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX obspart_geom_idx ON obspart_geom USING btree (obspart_geom_pk, validfrom, validto);


--
-- TOC entry 4834 (class 1259 OID 105344)
-- Name: obstacle_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX obstacle_idx ON obstacle USING btree (obstacle_pk, validfrom, validto);


--
-- TOC entry 4837 (class 1259 OID 105363)
-- Name: obstacleinssa_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX obstacleinssa_idx ON obstacleinssa USING btree (obstacleinssa_pk, validfrom, validto);


--
-- TOC entry 4840 (class 1259 OID 105382)
-- Name: obstaclepart_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX obstaclepart_idx ON obstaclepart USING btree (obstaclepart_pk, validfrom, validto);


--
-- TOC entry 4843 (class 1259 OID 105401)
-- Name: ocaoch_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ocaoch_idx ON ocaoch USING btree (ocaoch_pk, validfrom, validto);


--
-- TOC entry 4846 (class 1259 OID 105414)
-- Name: ocaochapptyp_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ocaochapptyp_idx ON ocaochapptyp USING btree (ocaochapptyp_pk, validfrom, validto);


--
-- TOC entry 4849 (class 1259 OID 105433)
-- Name: oil_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX oil_idx ON oil USING btree (oil_pk, validfrom, validto);


--
-- TOC entry 4852 (class 1259 OID 105452)
-- Name: orgauth_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX orgauth_idx ON orgauth USING btree (orgauth_pk, validfrom, validto);


--
-- TOC entry 4855 (class 1259 OID 105471)
-- Name: orgauthaddr_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX orgauthaddr_idx ON orgauthaddr USING btree (orgauthaddr_pk, validfrom, validto);


--
-- TOC entry 4858 (class 1259 OID 105490)
-- Name: orgauthassoc_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX orgauthassoc_idx ON orgauthassoc USING btree (orgauthassoc_pk, validfrom, validto);


--
-- TOC entry 4861 (class 1259 OID 105509)
-- Name: oxygen_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX oxygen_idx ON oxygen USING btree (oxygen_pk, validfrom, validto);


--
-- TOC entry 4864 (class 1259 OID 105522)
-- Name: pdflvl_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pdflvl_idx ON pdflvl USING btree (pdflvl_pk, validfrom, validto);


--
-- TOC entry 4867 (class 1259 OID 105541)
-- Name: pdflvlcol_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pdflvlcol_idx ON pdflvlcol USING btree (pdflvlcol_pk, validfrom, validto);


--
-- TOC entry 4870 (class 1259 OID 105561)
-- Name: pdflvltbl_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pdflvltbl_idx ON pdflvltbl USING btree (pdflvltbl_pk, validfrom, validto);


--
-- TOC entry 4873 (class 1259 OID 105580)
-- Name: proctransiction_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX proctransiction_idx ON proctransiction USING btree (proctransiction_pk, validfrom, validto);


--
-- TOC entry 4876 (class 1259 OID 105593)
-- Name: proctransleg_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX proctransleg_idx ON proctransleg USING btree (proctransleg_pk, validfrom, validto);


--
-- TOC entry 4879 (class 1259 OID 105612)
-- Name: prroute_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX prroute_idx ON prroute USING btree (prroute_pk, validfrom, validto);


--
-- TOC entry 4882 (class 1259 OID 105625)
-- Name: prroutetime_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX prroutetime_idx ON prroutetime USING btree (prroutetime_pk, validfrom, validto);


--
-- TOC entry 4885 (class 1259 OID 105638)
-- Name: radarcomponent_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX radarcomponent_idx ON radarcomponent USING btree (radarcomponent_pk, validfrom, validto);


--
-- TOC entry 4888 (class 1259 OID 105657)
-- Name: radarequipment_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX radarequipment_idx ON radarequipment USING btree (radarequipment_pk, validfrom, validto);


--
-- TOC entry 4891 (class 1259 OID 105676)
-- Name: radarsysrwylink_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX radarsysrwylink_idx ON radarsysrwylink USING btree (radarsysrwylink_pk, validfrom, validto);


--
-- TOC entry 4894 (class 1259 OID 105695)
-- Name: radarsystem_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX radarsystem_idx ON radarsystem USING btree (radarsystem_pk, validfrom, validto);


--
-- TOC entry 4897 (class 1259 OID 105714)
-- Name: radiosonde_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX radiosonde_idx ON radiosonde USING btree (radiosonde_pk, validfrom, validto);


--
-- TOC entry 4900 (class 1259 OID 105733)
-- Name: reflector_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX reflector_idx ON reflector USING btree (reflector_pk, validfrom, validto);


--
-- TOC entry 4903 (class 1259 OID 105752)
-- Name: road_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX road_idx ON road USING btree (road_pk, validfrom, validto);


--
-- TOC entry 4906 (class 1259 OID 105765)
-- Name: rteportion_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rteportion_idx ON rteportion USING btree (rteportion_pk, validfrom, validto);


--
-- TOC entry 4909 (class 1259 OID 105784)
-- Name: rteseguse_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rteseguse_idx ON rteseguse USING btree (rteseguse_pk, validfrom, validto);


--
-- TOC entry 4912 (class 1259 OID 105797)
-- Name: rteseguselvl_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rteseguselvl_idx ON rteseguselvl USING btree (rteseguselvl_pk, validfrom, validto);


--
-- TOC entry 4915 (class 1259 OID 105816)
-- Name: runway_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX runway_idx ON runway USING btree (runway_pk, validfrom, validto);


--
-- TOC entry 4918 (class 1259 OID 105835)
-- Name: rwdals_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwdals_idx ON rwdals USING btree (rwdals_pk, validfrom, validto);


--
-- TOC entry 4921 (class 1259 OID 105854)
-- Name: rwddecdist_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwddecdist_idx ON rwddecdist USING btree (rwddecdist_pk, validfrom, validto);


--
-- TOC entry 4924 (class 1259 OID 105873)
-- Name: rwdlgtgrp_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwdlgtgrp_idx ON rwdlgtgrp USING btree (rwdlgtgrp_pk, validfrom, validto);


--
-- TOC entry 4927 (class 1259 OID 105892)
-- Name: rwdlgtsys_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwdlgtsys_idx ON rwdlgtsys USING btree (rwdlgtsys_pk, validfrom, validto);


--
-- TOC entry 4933 (class 1259 OID 105924)
-- Name: rwdprtarea_geom_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwdprtarea_geom_idx ON rwdprtarea_geom USING btree (rwdprtarea_geom_pk, validfrom, validto);


--
-- TOC entry 4930 (class 1259 OID 105911)
-- Name: rwdprtarea_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwdprtarea_idx ON rwdprtarea USING btree (rwdprtarea_pk, validfrom, validto);


--
-- TOC entry 4936 (class 1259 OID 105943)
-- Name: rwyblastpad_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwyblastpad_idx ON rwyblastpad USING btree (rwyblastpad_pk, validfrom, validto);


--
-- TOC entry 4939 (class 1259 OID 105962)
-- Name: rwyclinept_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwyclinept_idx ON rwyclinept USING btree (rwyclinept_pk, validfrom, validto);


--
-- TOC entry 4942 (class 1259 OID 105981)
-- Name: rwydirection_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwydirection_idx ON rwydirection USING btree (rwydirection_pk, validfrom, validto);


--
-- TOC entry 4945 (class 1259 OID 106000)
-- Name: rwydirectnstr_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwydirectnstr_idx ON rwydirectnstr USING btree (rwydirectnstr_pk, validfrom, validto);


--
-- TOC entry 4948 (class 1259 OID 106019)
-- Name: rwyelement_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX rwyelement_idx ON rwyelement USING btree (rwyelement_pk, validfrom, validto);


--
-- TOC entry 4951 (class 1259 OID 106038)
-- Name: sdf_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX sdf_idx ON sdf USING btree (sdf_pk, validfrom, validto);


--
-- TOC entry 4954 (class 1259 OID 106057)
-- Name: segmnt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX segmnt_idx ON segmnt USING btree (segmnt_pk, validfrom, validto);


--
-- TOC entry 4957 (class 1259 OID 106076)
-- Name: service_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX service_idx ON service USING btree (service_pk, validfrom, validto);


--
-- TOC entry 4960 (class 1259 OID 106095)
-- Name: sigpointinas_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX sigpointinas_idx ON sigpointinas USING btree (sigpointinas_pk, validfrom, validto);


--
-- TOC entry 4963 (class 1259 OID 106114)
-- Name: specialdate_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX specialdate_idx ON specialdate USING btree (specialdate_pk, validfrom, validto);


--
-- TOC entry 4966 (class 1259 OID 106133)
-- Name: specnavstn_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX specnavstn_idx ON specnavstn USING btree (specnavstn_pk, validfrom, validto);


--
-- TOC entry 4969 (class 1259 OID 106152)
-- Name: specnavsys_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX specnavsys_idx ON specnavsys USING btree (specnavsys_pk, validfrom, validto);


--
-- TOC entry 4972 (class 1259 OID 106171)
-- Name: ssa_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ssa_idx ON ssa USING btree (ssa_pk, validfrom, validto);


--
-- TOC entry 4975 (class 1259 OID 106184)
-- Name: ssapath_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ssapath_idx ON ssapath USING btree (ssapath_pk, validfrom, validto);


--
-- TOC entry 4978 (class 1259 OID 106203)
-- Name: ssapathleg_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ssapathleg_idx ON ssapathleg USING btree (ssapathleg_pk, validfrom, validto);


--
-- TOC entry 4981 (class 1259 OID 106216)
-- Name: ssapathlegarea_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ssapathlegarea_idx ON ssapathlegarea USING btree (ssapathlegarea_pk, validfrom, validto);


--
-- TOC entry 4984 (class 1259 OID 106235)
-- Name: ssaseg_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ssaseg_idx ON ssaseg USING btree (ssaseg_pk, validfrom, validto);


--
-- TOC entry 4987 (class 1259 OID 106254)
-- Name: ssausage_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ssausage_idx ON ssausage USING btree (ssausage_pk, validfrom, validto);


--
-- TOC entry 4990 (class 1259 OID 106273)
-- Name: surveyctrlpt_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX surveyctrlpt_idx ON surveyctrlpt USING btree (surveyctrlpt_pk, validfrom, validto);


--
-- TOC entry 4993 (class 1259 OID 106292)
-- Name: svcinasp_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX svcinasp_idx ON svcinasp USING btree (svcinasp_pk, validfrom, validto);


--
-- TOC entry 4996 (class 1259 OID 106311)
-- Name: svconadhp_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX svconadhp_idx ON svconadhp USING btree (svconadhp_pk, validfrom, validto);


--
-- TOC entry 4999 (class 1259 OID 106330)
-- Name: svconhldgproc_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX svconhldgproc_idx ON svconhldgproc USING btree (svconhldgproc_pk, validfrom, validto);


--
-- TOC entry 5002 (class 1259 OID 106349)
-- Name: svconrteseg_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX svconrteseg_idx ON svconrteseg USING btree (svconrteseg_pk, validfrom, validto);


--
-- TOC entry 5005 (class 1259 OID 106368)
-- Name: svconssa_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX svconssa_idx ON svconssa USING btree (svconssa_pk, validfrom, validto);


--
-- TOC entry 5008 (class 1259 OID 106387)
-- Name: tacan_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tacan_idx ON tacan USING btree (tacan_pk, validfrom, validto);


--
-- TOC entry 5011 (class 1259 OID 106406)
-- Name: tacanlimitatn_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tacanlimitatn_idx ON tacanlimitatn USING btree (tacanlimitatn_pk, validfrom, validto);


--
-- TOC entry 5014 (class 1259 OID 106425)
-- Name: tfcflowrestr_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tfcflowrestr_idx ON tfcflowrestr USING btree (tfcflowrestr_pk, validfrom, validto);


--
-- TOC entry 5017 (class 1259 OID 106438)
-- Name: tfcflowrte_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tfcflowrte_idx ON tfcflowrte USING btree (tfcflowrte_pk, validfrom, validto);


--
-- TOC entry 5020 (class 1259 OID 106451)
-- Name: tfcflwrtelem_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tfcflwrtelem_idx ON tfcflwrtelem USING btree (tfcflwrtelem_pk, validfrom, validto);


--
-- TOC entry 5023 (class 1259 OID 106464)
-- Name: tfcflwrtellv_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tfcflwrtellv_idx ON tfcflwrtellv USING btree (tfcflwrtellv_pk, validfrom, validto);


--
-- TOC entry 5026 (class 1259 OID 106483)
-- Name: time_template_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX time_template_idx ON time_template USING btree (time_template_pk, validfrom, validto);


--
-- TOC entry 5029 (class 1259 OID 106502)
-- Name: timesheet_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX timesheet_idx ON timesheet USING btree (timesheet_pk, validfrom, validto);


--
-- TOC entry 5035 (class 1259 OID 106534)
-- Name: tlof_geom_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tlof_geom_idx ON tlof_geom USING btree (tlof_geom_pk, validfrom, validto);


--
-- TOC entry 5032 (class 1259 OID 106521)
-- Name: tlof_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tlof_idx ON tlof USING btree (tlof_pk, validfrom, validto);


--
-- TOC entry 5038 (class 1259 OID 106553)
-- Name: tloflgtsys_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tloflgtsys_idx ON tloflgtsys USING btree (tloflgtsys_pk, validfrom, validto);


--
-- TOC entry 5041 (class 1259 OID 106572)
-- Name: tlofsafearea_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tlofsafearea_idx ON tlofsafearea USING btree (tlofsafearea_pk, validfrom, validto);


--
-- TOC entry 5044 (class 1259 OID 106585)
-- Name: tlofsarea_geom_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX tlofsarea_geom_idx ON tlofsarea_geom USING btree (tlofsarea_geom_pk, validfrom, validto);


--
-- TOC entry 5047 (class 1259 OID 106604)
-- Name: twy_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX twy_idx ON twy USING btree (twy_pk, validfrom, validto);


--
-- TOC entry 5050 (class 1259 OID 106623)
-- Name: twyelement_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX twyelement_idx ON twyelement USING btree (twyelement_pk, validfrom, validto);


--
-- TOC entry 5053 (class 1259 OID 106642)
-- Name: twyhldpos_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX twyhldpos_idx ON twyhldpos USING btree (twyhldpos_pk, validfrom, validto);


--
-- TOC entry 5056 (class 1259 OID 106661)
-- Name: twyintrsectn_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX twyintrsectn_idx ON twyintrsectn USING btree (twyintrsectn_pk, validfrom, validto);


--
-- TOC entry 5059 (class 1259 OID 106680)
-- Name: twylgtsys_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX twylgtsys_idx ON twylgtsys USING btree (twylgtsys_pk, validfrom, validto);


--
-- TOC entry 5062 (class 1259 OID 106699)
-- Name: unit_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX unit_idx ON unit USING btree (unit_pk, validfrom, validto);


--
-- TOC entry 5065 (class 1259 OID 106718)
-- Name: unitaddress_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX unitaddress_idx ON unitaddress USING btree (unitaddress_pk, validfrom, validto);


--
-- TOC entry 5068 (class 1259 OID 106737)
-- Name: unitassoc_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX unitassoc_idx ON unitassoc USING btree (unitassoc_pk, validfrom, validto);


--
-- TOC entry 5071 (class 1259 OID 106756)
-- Name: vor_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX vor_idx ON vor USING btree (vor_pk, validfrom, validto);


--
-- TOC entry 5074 (class 1259 OID 106775)
-- Name: vorlimitation_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX vorlimitation_idx ON vorlimitation USING btree (vorlimitation_pk, validfrom, validto);


--
-- TOC entry 5077 (class 1259 OID 106794)
-- Name: waypoint_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX waypoint_idx ON waypoint USING btree (waypoint_pk, validfrom, validto);


--
-- TOC entry 5080 (class 1259 OID 106813)
-- Name: workarea_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX workarea_idx ON workarea USING btree (workarea_pk, validfrom, validto);


-- Completed on 2013-07-12 18:15:25

--
-- PostgreSQL database dump complete
--

