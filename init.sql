--
-- PostgreSQL database dump
--

-- Dumped from database version 10.5 (Debian 10.5-1)
-- Dumped by pg_dump version 11.1 (Debian 11.1-2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: annex; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.annex (
    annex_id integer NOT NULL,
    annex_number character varying(255),
    new_due_date timestamp without time zone,
    new_value numeric(19,2),
    contract_id integer
);


ALTER TABLE public.annex OWNER TO postgres;

--
-- Name: area_of_expertise; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.area_of_expertise (
    area_of_expertise_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.area_of_expertise OWNER TO postgres;

--
-- Name: arranged_job; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.arranged_job (
    arranged_job_id integer NOT NULL,
    arranged_job_number character varying(255),
    due_date timestamp without time zone,
    is_done boolean,
    is_operational boolean,
    job_description character varying(255),
    job_type_flag boolean,
    name character varying(255),
    order_form_date timestamp without time zone,
    order_form_deadline timestamp without time zone,
    order_form_value double precision,
    planning_deadline timestamp without time zone,
    predmer_date timestamp without time zone,
    start_date timestamp without time zone,
    status character varying(255),
    chief_user_id integer,
    contract_id integer,
    location_id integer,
    preliminary_plan_id integer,
    project_manager_user_id integer,
    sector_id integer,
    creation_timestamp timestamp without time zone,
    job_started boolean,
    value double precision,
    ongoing_plan_end_date timestamp without time zone,
    ongoing_plan_start_date timestamp without time zone
);


ALTER TABLE public.arranged_job OWNER TO postgres;

--
-- Name: arranged_job_delay; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.arranged_job_delay (
    arranged_job_delay_id integer NOT NULL,
    delay_date timestamp without time zone,
    delay_reason character varying(255),
    arranged_job_id integer
);


ALTER TABLE public.arranged_job_delay OWNER TO postgres;

--
-- Name: bank_account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bank_account (
    bank_account_id integer NOT NULL,
    account_number character varying(255),
    bank_name character varying(255),
    investor_id integer
);


ALTER TABLE public.bank_account OWNER TO postgres;

--
-- Name: cache; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cache (
    cache_id integer NOT NULL,
    contract_counter integer,
    offer_counter integer,
    order_form_counter integer,
    project_counter integer,
    year integer NOT NULL,
    investor_counter integer
);


ALTER TABLE public.cache OWNER TO postgres;

--
-- Name: calculant; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.calculant (
    calculant_id integer NOT NULL,
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255)
);


ALTER TABLE public.calculant OWNER TO postgres;

--
-- Name: city; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.city (
    city_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.city OWNER TO postgres;

--
-- Name: construction_insurance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.construction_insurance (
    construction_insurance_id integer NOT NULL,
    acquired boolean NOT NULL,
    name character varying(255),
    sent_to_investor boolean,
    offer_id integer
);


ALTER TABLE public.construction_insurance OWNER TO postgres;

--
-- Name: contact; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contact (
    contact_id integer NOT NULL,
    email character varying(255),
    firstname_lastname character varying(255),
    phone character varying(255),
    "position" character varying(255),
    investor_id integer
);


ALTER TABLE public.contact OWNER TO postgres;

--
-- Name: contract; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contract (
    contract_id integer NOT NULL,
    contract_number character varying(255),
    creation_date timestamp without time zone,
    finish_date timestamp without time zone,
    gala_number character varying(255),
    handover_timestamp timestamp without time zone,
    invoiced_value numeric(19,2),
    mis_number character varying(255),
    status character varying(255),
    investor_id integer,
    offer_id integer,
    person_responsible_user_id integer,
    contract_code character varying(255),
    registered_number character varying(255),
    estimated_days character varying(255)
);


ALTER TABLE public.contract OWNER TO postgres;

--
-- Name: contract_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contract_type (
    contract_type_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.contract_type OWNER TO postgres;

--
-- Name: contractor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contractor (
    contractor_id integer NOT NULL,
    average_grade double precision,
    name character varying(255),
    pib character varying(255),
    pin character varying(255),
    location_id integer
);


ALTER TABLE public.contractor OWNER TO postgres;

--
-- Name: contractor_area_of_expertise; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contractor_area_of_expertise (
    grade double precision NOT NULL,
    area_of_expertise_id integer NOT NULL,
    contractor_id integer NOT NULL
);


ALTER TABLE public.contractor_area_of_expertise OWNER TO postgres;

--
-- Name: contractor_bank_account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contractor_bank_account (
    contractor_bank_account_id integer NOT NULL,
    account_number character varying(255),
    bank_name character varying(255),
    contractor_id integer
);


ALTER TABLE public.contractor_bank_account OWNER TO postgres;

--
-- Name: contractor_contact; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contractor_contact (
    contractor_contact_id integer NOT NULL,
    email character varying(255),
    firstname_lastname character varying(255),
    phone character varying(255),
    "position" character varying(255),
    contractor_id integer
);


ALTER TABLE public.contractor_contact OWNER TO postgres;

--
-- Name: contractor_work_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contractor_work_type (
    contractor_id integer NOT NULL,
    work_type_id integer NOT NULL
);


ALTER TABLE public.contractor_work_type OWNER TO postgres;

--
-- Name: employer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employer (
    employer_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.employer OWNER TO postgres;

--
-- Name: equipment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.equipment (
    equipment_id integer NOT NULL,
    inventory_number character varying(255),
    measurement_unit character varying(255),
    name character varying(255),
    note character varying(255),
    producer character varying(255),
    equipment_type_id integer
);


ALTER TABLE public.equipment OWNER TO postgres;

--
-- Name: equipment_project_day; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.equipment_project_day (
    date timestamp without time zone NOT NULL,
    status character varying(255),
    time_of_arrival timestamp without time zone,
    time_of_departure timestamp without time zone,
    equipment_id integer NOT NULL,
    arranged_job_id integer NOT NULL,
    chief_user_id integer,
    sector_id integer
);


ALTER TABLE public.equipment_project_day OWNER TO postgres;

--
-- Name: equipment_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.equipment_type (
    equipment_type_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.equipment_type OWNER TO postgres;

--
-- Name: financial_instrument; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.financial_instrument (
    financial_instrument_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.financial_instrument OWNER TO postgres;

--
-- Name: financial_security_asset; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.financial_security_asset (
    financial_security_asset_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.financial_security_asset OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: human_resource; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.human_resource (
    human_resource_id integer NOT NULL,
    additional_skill character varying(255),
    date_of_birth timestamp without time zone,
    email character varying(255),
    firstname_lastname character varying(255),
    id_number character varying(255),
    is_height_work boolean,
    phone character varying(255),
    pin character varying(255),
    status character varying(255),
    working_class character varying(255),
    city_id integer,
    employer_id integer,
    job_type_id integer,
    sector_id integer
);


ALTER TABLE public.human_resource OWNER TO postgres;

--
-- Name: investor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.investor (
    investor_id integer NOT NULL,
    address character varying(255),
    branch_of_economic_activity character varying(255),
    investor_number character varying(255),
    name character varying(255),
    pib character varying(255),
    pin character varying(255),
    city_id integer
);


ALTER TABLE public.investor OWNER TO postgres;

--
-- Name: investor_bank_accounts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.investor_bank_accounts (
    investor_investor_id integer NOT NULL,
    bank_accounts_bank_account_id integer NOT NULL
);


ALTER TABLE public.investor_bank_accounts OWNER TO postgres;

--
-- Name: investor_contacts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.investor_contacts (
    investor_investor_id integer NOT NULL,
    contacts_contact_id integer NOT NULL
);


ALTER TABLE public.investor_contacts OWNER TO postgres;

--
-- Name: investor_real_estates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.investor_real_estates (
    investor_investor_id integer NOT NULL,
    real_estates_real_estate_id integer NOT NULL
);


ALTER TABLE public.investor_real_estates OWNER TO postgres;

--
-- Name: invoice; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.invoice (
    invoice_id integer NOT NULL,
    construction_book_signed_date timestamp without time zone,
    date timestamp without time zone,
    date_signed timestamp without time zone,
    explanation character varying(255),
    is_construction_book_signed boolean,
    is_sent boolean,
    is_signed boolean,
    situation_number character varying(255),
    situation_number_date timestamp without time zone,
    situation_number_status character varying(255),
    value double precision NOT NULL,
    arranged_job_id integer
);


ALTER TABLE public.invoice OWNER TO postgres;

--
-- Name: job_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.job_type (
    job_type_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.job_type OWNER TO postgres;

--
-- Name: location; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.location (
    location_id integer NOT NULL,
    name character varying(255),
    address character varying(255),
    city_id integer,
    real_estate_id integer
);


ALTER TABLE public.location OWNER TO postgres;

--
-- Name: mechanization; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mechanization (
    mechanization_id integer NOT NULL,
    carry_weight double precision,
    name character varying(255),
    note character varying(255),
    producer character varying(255),
    registration_date timestamp without time zone,
    registration_number character varying(255),
    service_date timestamp without time zone,
    status character varying(255),
    year_of_production integer,
    mechanization_group_id integer,
    mechanization_type_id integer
);


ALTER TABLE public.mechanization OWNER TO postgres;

--
-- Name: mechanization_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mechanization_group (
    mechanization_group_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.mechanization_group OWNER TO postgres;

--
-- Name: mechanization_project_day; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mechanization_project_day (
    date timestamp without time zone NOT NULL,
    status character varying(255),
    time_of_arrival timestamp without time zone,
    time_of_departure timestamp without time zone,
    mechanization_id integer NOT NULL,
    arranged_job_id integer NOT NULL,
    chief_user_id integer,
    sector_id integer
);


ALTER TABLE public.mechanization_project_day OWNER TO postgres;

--
-- Name: mechanization_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mechanization_type (
    mechanization_type_id integer NOT NULL,
    name character varying(255),
    mechanization_group_id integer
);


ALTER TABLE public.mechanization_type OWNER TO postgres;

--
-- Name: offer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.offer (
    offer_id integer NOT NULL,
    competitor_offer_value numeric(19,2),
    competitor_who_won character varying(255),
    competitor_winning_reason character varying(255),
    creation_timestamp timestamp without time zone,
    dismissed_reason character varying(255),
    estimated_value numeric(19,2),
    name character varying(255),
    note character varying(255),
    offer_due_date timestamp without time zone,
    offer_number character varying(255),
    offer_value numeric(19,2),
    percentage_gain double precision,
    status character varying(255),
    tendering_timestamp timestamp without time zone,
    work_description character varying(255),
    calculant_id integer,
    city_id integer,
    contract_id integer,
    contract_type_id integer,
    creator_user_id integer,
    investor_id integer,
    offer_type_id integer,
    sector_id integer,
    user_id_who_rejected integer,
    contact_id integer
);


ALTER TABLE public.offer OWNER TO postgres;

--
-- Name: offer_financial_security_asset; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.offer_financial_security_asset (
    acquired boolean NOT NULL,
    sent_to_investor boolean,
    financial_security_asset_id integer NOT NULL,
    offer_id integer NOT NULL,
    value character varying(255)
);


ALTER TABLE public.offer_financial_security_asset OWNER TO postgres;

--
-- Name: offer_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.offer_type (
    offer_type_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.offer_type OWNER TO postgres;

--
-- Name: phase; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.phase (
    phase_id integer NOT NULL,
    base_plan_end_date timestamp without time zone,
    base_plan_start_date timestamp without time zone,
    coefficient double precision NOT NULL,
    grade double precision NOT NULL,
    is_done boolean,
    ongoing_plan_end_date timestamp without time zone,
    ongoing_plan_start_date timestamp without time zone,
    phase_name character varying(255),
    arranged_job_id integer,
    description character varying(255),
    percentage_done double precision
);


ALTER TABLE public.phase OWNER TO postgres;

--
-- Name: phase_contractor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.phase_contractor (
    added boolean NOT NULL,
    reason character varying(255),
    phase_id integer NOT NULL,
    contractor_id integer NOT NULL,
    area_of_expertise_id integer
);


ALTER TABLE public.phase_contractor OWNER TO postgres;

--
-- Name: preliminary_plan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.preliminary_plan (
    preliminary_plan_id integer NOT NULL,
    creation_timestamp timestamp without time zone,
    arranged_job_id integer
);


ALTER TABLE public.preliminary_plan OWNER TO postgres;

--
-- Name: preliminary_plan_employee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.preliminary_plan_employee (
    preliminary_plan_employee_id integer NOT NULL,
    finish_date timestamp without time zone,
    is_height_work boolean,
    needed_number integer,
    start_date timestamp without time zone,
    job_type_id integer,
    preliminary_plan_id integer,
    arranged_job_id integer
);


ALTER TABLE public.preliminary_plan_employee OWNER TO postgres;

--
-- Name: preliminary_plan_equipment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.preliminary_plan_equipment (
    preliminary_plan_equipment_id integer NOT NULL,
    finish_date timestamp without time zone,
    needed_number integer,
    start_date timestamp without time zone,
    equipment_type_id integer,
    preliminary_plan_id integer,
    arranged_job_id integer
);


ALTER TABLE public.preliminary_plan_equipment OWNER TO postgres;

--
-- Name: preliminary_plan_mechanization; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.preliminary_plan_mechanization (
    preliminary_plan_mechanization_id integer NOT NULL,
    finish_date timestamp without time zone,
    needed_number integer,
    start_date timestamp without time zone,
    mechanization_group_id integer,
    mechanization_type_id integer,
    preliminary_plan_id integer,
    arranged_job_id integer
);


ALTER TABLE public.preliminary_plan_mechanization OWNER TO postgres;

--
-- Name: real_estate; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.real_estate (
    real_estate_id integer NOT NULL,
    name character varying(255),
    investor_id integer,
    location_id integer,
    address character varying(255),
    city_id integer
);


ALTER TABLE public.real_estate OWNER TO postgres;

--
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    role_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.role OWNER TO postgres;

--
-- Name: sector; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sector (
    sector_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.sector OWNER TO postgres;

--
-- Name: user_jadran; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_jadran (
    user_id integer NOT NULL,
    firstname_lastname character varying(255),
    password character varying(255),
    phone character varying(255),
    username character varying(255),
    sector_id integer
);


ALTER TABLE public.user_jadran OWNER TO postgres;

--
-- Name: user_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_role (
    user_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE public.user_role OWNER TO postgres;

--
-- Name: work_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.work_type (
    work_type_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.work_type OWNER TO postgres;

--
-- Name: worker_project_day; Type: TABLE; Schema: public; Owner: nikola
--

CREATE TABLE public.worker_project_day (
    date timestamp without time zone NOT NULL,
    time_of_arrival timestamp without time zone,
    time_of_departure timestamp without time zone,
    value integer,
    human_resource_id integer NOT NULL,
    arranged_job_id integer NOT NULL,
    sector_id integer
);


ALTER TABLE public.worker_project_day OWNER TO nikola;

--
-- Data for Name: annex; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.annex (annex_id, annex_number, new_due_date, new_value, contract_id) FROM stdin;
\.


--
-- Data for Name: area_of_expertise; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.area_of_expertise (area_of_expertise_id, name) FROM stdin;
1	Elektro radovi\n
2	Gradjevinski radovi
3	Molersko-farbarski radovi
4	Inzenjering
\.


--
-- Data for Name: arranged_job; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.arranged_job (arranged_job_id, arranged_job_number, due_date, is_done, is_operational, job_description, job_type_flag, name, order_form_date, order_form_deadline, order_form_value, planning_deadline, predmer_date, start_date, status, chief_user_id, contract_id, location_id, preliminary_plan_id, project_manager_user_id, sector_id, creation_timestamp, job_started, value, ongoing_plan_end_date, ongoing_plan_start_date) FROM stdin;
229	U-7/2019- P12	\N	f	f	POtrebno je peskirati branu	f	AKZ - Peskiranje brane Dunav	\N	\N	0	2019-03-14 01:00:00	\N	\N	\N	14	122	230	\N	10	2	\N	t	0	\N	\N
226	U-9/2019- P11	2019-03-07 01:00:00	f	f	Potrebno je izmestiti namestaj, i renovirati sve	f	RTS - V sprat - renoviranje	\N	\N	0	2019-02-23 01:00:00	\N	2019-03-02 01:00:00	Preliminarni plan	13	123	227	\N	9	1	2019-03-01 14:58:51.231	f	0	\N	\N
\.


--
-- Data for Name: arranged_job_delay; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.arranged_job_delay (arranged_job_delay_id, delay_date, delay_reason, arranged_job_id) FROM stdin;
\.


--
-- Data for Name: bank_account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bank_account (bank_account_id, account_number, bank_name, investor_id) FROM stdin;
24	160-15678-12	Banca Intesa	\N
47	190=31313-121	Banca Intesa	\N
79	105-13245-153	Banca Intesa 	\N
108	145-32133-12	AIK Banka	\N
125	145-12343-45	Banca Intesa	\N
\.


--
-- Data for Name: cache; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cache (cache_id, contract_counter, offer_counter, order_form_counter, project_counter, year, investor_counter) FROM stdin;
1	10	7	1	13	2019	6
\.


--
-- Data for Name: calculant; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.calculant (calculant_id, email, firstname, lastname) FROM stdin;
\.


--
-- Data for Name: city; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.city (city_id, name) FROM stdin;
1	Beograd
2	Novi sad
\.


--
-- Data for Name: construction_insurance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.construction_insurance (construction_insurance_id, acquired, name, sent_to_investor, offer_id) FROM stdin;
131	f	Oprema na gradilištu	f	129
132	f	Profesionalna odgovornost	f	129
133	f	Nešto treće	f	129
\.


--
-- Data for Name: contact; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contact (contact_id, email, firstname_lastname, phone, "position", investor_id) FROM stdin;
23	bojovic@ems.rs	Kristina Bojović	06532482	HR direktor	\N
27	cebic@ems.rs	Branko Ćebić	06794999	Direktor logistike	\N
46		\N			\N
78	majstorovic@smatsa.rs	Milenko Majstorović	534234	Direktor kvaliteta	\N
107	ajdoda	Vladislav Lučić	43141	Tehnički direktor	\N
124	komrakov@rts.rs	Komrakov	987324	Urednik	\N
\.


--
-- Data for Name: contract; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contract (contract_id, contract_number, creation_date, finish_date, gala_number, handover_timestamp, invoiced_value, mis_number, status, investor_id, offer_id, person_responsible_user_id, contract_code, registered_number, estimated_days) FROM stdin;
130	U-9/2019	2019-02-20 01:00:00	\N	\N	2019-02-26 01:00:00	\N	\N	Aktivan	\N	129	5	U-9/2019	\N	150 dana od uvođenja u posao
122	U-7/2019	2019-02-24 18:37:34.402	\N	\N	2019-02-26 01:00:00	\N	\N	Kreiran	\N	112	6	\N	\N	\N
123	U-8/2019	2019-02-23 01:00:00	2021-02-23 01:00:00	\N	2019-02-28 01:00:00	\N	\N	Aktivan	\N	45	6	JN 16 04 2019	\N	\N
\.


--
-- Data for Name: contract_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contract_type (contract_type_id, name) FROM stdin;
1	Jednokratni ugovor
2	Okvirni sporazum
\.


--
-- Data for Name: contractor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contractor (contractor_id, average_grade, name, pib, pin, location_id) FROM stdin;
166	0	davwdv	3423	2342	165
170	0	dawdv	4	4	169
180	0	fsevf	34534	34534	179
186	0	Pera	343	324234	185
1	4	q	1	1	300
2	5	w	2	2	301
3	3	e	3	3	302
194	0	Pera debil	123213	213123	193
200	0	safjopajf	123	124	199
\.


--
-- Data for Name: contractor_area_of_expertise; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contractor_area_of_expertise (grade, area_of_expertise_id, contractor_id) FROM stdin;
0	1	200
0	4	200
\.


--
-- Data for Name: contractor_bank_account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contractor_bank_account (contractor_bank_account_id, account_number, bank_name, contractor_id) FROM stdin;
167	2342	advwdva	\N
171	4	dabbd	\N
181	4353	fsevfs	\N
184	4353	fsevfs	180
187	324234	aik	\N
190	324234	aik	186
195			\N
198			194
201	132	213	\N
204	132	213	200
\.


--
-- Data for Name: contractor_contact; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contractor_contact (contractor_contact_id, email, firstname_lastname, phone, "position", contractor_id) FROM stdin;
168	dawvdab	davwd	23423	adwbwb	\N
172	dvawv	vdavw	4	dba	\N
182	sebfsb	fsefb	34534	fsebsfb	\N
183	sebfsb	fsefb	34534	fsebsfb	180
188	pera	pera	345	pera	\N
189	pera	pera	345	pera	186
196		\N			\N
197		\N			194
202		\N			\N
203		\N			200
\.


--
-- Data for Name: contractor_work_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contractor_work_type (contractor_id, work_type_id) FROM stdin;
1	3
2	2
3	1
1	2
\.


--
-- Data for Name: employer; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employer (employer_id, name) FROM stdin;
1	Jadran
2	JP EPS
\.


--
-- Data for Name: equipment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.equipment (equipment_id, inventory_number, measurement_unit, name, note, producer, equipment_type_id) FROM stdin;
205			\N			\N
206	eqw	qew	\N	qew	qwe	\N
207	eqw	qew	\N	qew	qwe	\N
208	eqw	qew	\N	qew	qwe	\N
209	eqw	qew	\N	qew	qwe	\N
\.


--
-- Data for Name: equipment_project_day; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.equipment_project_day (date, status, time_of_arrival, time_of_departure, equipment_id, arranged_job_id, chief_user_id, sector_id) FROM stdin;
\.


--
-- Data for Name: equipment_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.equipment_type (equipment_type_id, name) FROM stdin;
1	Alat
2	Zastita
3	Ne znam
\.


--
-- Data for Name: financial_instrument; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.financial_instrument (financial_instrument_id, name) FROM stdin;
\.


--
-- Data for Name: financial_security_asset; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.financial_security_asset (financial_security_asset_id, name) FROM stdin;
1	Ozbiljnost ponude
2	Povracaj avansa
3	Dobro izvrsenje
4	Garantni rok
\.


--
-- Data for Name: human_resource; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.human_resource (human_resource_id, additional_skill, date_of_birth, email, firstname_lastname, id_number, is_height_work, phone, pin, status, working_class, city_id, employer_id, job_type_id, sector_id) FROM stdin;
1	Spretan brda	\N	mfasdm@gmail.com	Pera	ID NJEGOV	f	124124124	124124124124124	Aktivan	Klasa A	1	1	1	1
\.


--
-- Data for Name: investor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.investor (investor_id, address, branch_of_economic_activity, investor_number, name, pib, pin, city_id) FROM stdin;
26	Kneza Miloša 11	Prenos EE	INV 1	Elektromreža Srbije - EMS AD	10954622	2244657	1
49	Batajnicki drum bb	Proizvodnja	INV 2	Bosh	408314	4831094	1
81	Surčinski put bb	Kontrola letenja	INV 3	SMATSA	10945382	1093829	1
110	Petrovaradin bb	Montaža	INV 4	Goša Montaža 	31935135	12349831	2
127	Takovska 1	Televizija	INV 5	RTS Beograd	1092842	4372375	1
\.


--
-- Data for Name: investor_bank_accounts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.investor_bank_accounts (investor_investor_id, bank_accounts_bank_account_id) FROM stdin;
26	24
49	47
81	79
110	108
127	125
\.


--
-- Data for Name: investor_contacts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.investor_contacts (investor_investor_id, contacts_contact_id) FROM stdin;
26	23
26	27
49	46
81	78
110	107
127	124
\.


--
-- Data for Name: investor_real_estates; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.investor_real_estates (investor_investor_id, real_estates_real_estate_id) FROM stdin;
26	25
26	28
49	48
81	80
110	109
127	126
\.


--
-- Data for Name: invoice; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.invoice (invoice_id, construction_book_signed_date, date, date_signed, explanation, is_construction_book_signed, is_sent, is_signed, situation_number, situation_number_date, situation_number_status, value, arranged_job_id) FROM stdin;
\.


--
-- Data for Name: job_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.job_type (job_type_id, name) FROM stdin;
1	Moler-farbar
2	Pomocni radnik
3	Zidar
4	Vodoinstalater
5	Gipsar
6	Metalo-molero farbar
7	Peskirer
8	Rukovalac motornom testerom
9	Rukovalac tarupom-traktorom
10	Elektroinstalater
11	Elektromonter
\.


--
-- Data for Name: location; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.location (location_id, name, address, city_id, real_estate_id) FROM stdin;
165	\N	adwvawv	2	\N
169	\N	adbwab	2	\N
179	\N	sevfsve	2	\N
185	\N	opera	1	\N
300	\N	sdfsdf	2	\N
301	\N	qwer	1	\N
302	\N	wqerwqr	2	\N
303	\N	qwerqwerqe	1	\N
193	\N	iashfosaihf	1	\N
199	\N	p	1	\N
227	\N	Takovska 11	1	\N
230	\N	Temerinski put BB	2	\N
\.


--
-- Data for Name: mechanization; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mechanization (mechanization_id, carry_weight, name, note, producer, registration_date, registration_number, service_date, status, year_of_production, mechanization_group_id, mechanization_type_id) FROM stdin;
210	20000	\N		Mercedes	2019-02-21 01:00:00	BG1022UG	2019-02-28 01:00:00	Aktivna	2015	\N	15
\.


--
-- Data for Name: mechanization_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mechanization_group (mechanization_group_id, name) FROM stdin;
1	Građevinska mašina
2	Kamion
3	Vozilo
\.


--
-- Data for Name: mechanization_project_day; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mechanization_project_day (date, status, time_of_arrival, time_of_departure, mechanization_id, arranged_job_id, chief_user_id, sector_id) FROM stdin;
\.


--
-- Data for Name: mechanization_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mechanization_type (mechanization_type_id, name, mechanization_group_id) FROM stdin;
1	Kombinirka	1
2	Bager	1
3	Mini bager	1
4	Telehender	1
5	Kamion sa radnom platformom	1
6	Pumpa za beton	1
7	Kran	1
8	Vertikalna dizalica	1
9	Traktor	1
10	Kompresor	1
11	Teski teretni kamion	2
12	Laki teretni kamion	2
13	Prikolica	2
14	Putnicko vozilo	3
15	Terensko vozilo	3
\.


--
-- Data for Name: offer; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.offer (offer_id, competitor_offer_value, competitor_who_won, competitor_winning_reason, creation_timestamp, dismissed_reason, estimated_value, name, note, offer_due_date, offer_number, offer_value, percentage_gain, status, tendering_timestamp, work_description, calculant_id, city_id, contract_id, contract_type_id, creator_user_id, investor_id, offer_type_id, sector_id, user_id_who_rejected, contact_id) FROM stdin;
85	\N	\N	\N	\N		\N	Renoviranje stare zgrade	Napomena	2019-02-27 01:00:00	P-2/2019	4500000.00	17	Izgubljeno	2019-02-13 01:00:00	Opis	\N	1	\N	1	20	81	84	1	\N	\N
87	\N	\N	\N	\N		\N	Bosch - Peskiranje fabrike	Ništa	2019-02-27 01:00:00	P-3/2019	7890000.00	25	Poništeno	2019-02-21 01:00:00	Potrebno je peskirati stubove	\N	2	\N	1	6	49	94	1	\N	\N
45	\N	\N	\N	\N		\N	EMS dalekovodi Pogon Beograd	Bez napomene	2019-02-28 01:00:00	P-1/2019	113000000.00	34	Ugovoreno	2019-02-13 01:00:00	Farbarnje i peskiranje	\N	1	123	2	20	26	83	2	\N	\N
129	\N	\N	\N	\N		\N	RTS - 5. sprat - renoviranje		2019-02-01 01:00:00	P-5/2019	54107521.00	19	Ugovoreno	\N	Ceo peti sprat uradiiti	\N	1	130	1	20	127	128	1	\N	\N
112	\N	\N	\N	\N		5000000.00	Šebešfok i Bezdan 180-026		2019-02-26 01:00:00	P-4/2019	5000000.00	24	Dobijeno	\N	Pitaj Boga šta je ovo	\N	2	122	1	6	110	111	2	\N	\N
263	\N	\N	\N	\N		1.00	Nzm		2019-02-27 01:00:00	P-6/2019	1.00	1	U izradi	2019-02-22 01:00:00	sve	\N	1	\N	1	20	110	262	1	\N	\N
\.


--
-- Data for Name: offer_financial_security_asset; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.offer_financial_security_asset (acquired, sent_to_investor, financial_security_asset_id, offer_id, value) FROM stdin;
f	f	2	129	2
f	f	1	129	1
f	f	3	129	2
f	f	4	129	2
\.


--
-- Data for Name: offer_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.offer_type (offer_type_id, name) FROM stdin;
44	JN
82	JN
83	JN
84	JN
86	Privatni investitor
94	JN
111	Privatni investitor
128	JN
262	JN
\.


--
-- Data for Name: phase; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.phase (phase_id, base_plan_end_date, base_plan_start_date, coefficient, grade, is_done, ongoing_plan_end_date, ongoing_plan_start_date, phase_name, arranged_job_id, description, percentage_done) FROM stdin;
261	2019-02-21 01:00:00	2019-02-05 01:00:00	2	0	f	\N	\N	Faza1	226	\N	0
264	2019-03-30 01:00:00	2019-03-12 01:00:00	0	0	f	\N	\N	Elektroradovi	226		0
265	2019-03-19 01:00:00	2019-03-19 01:00:00	0	0	f	\N	\N	Zavrsnii radovi	226		0
\.


--
-- Data for Name: phase_contractor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.phase_contractor (added, reason, phase_id, contractor_id, area_of_expertise_id) FROM stdin;
f		261	200	4
\.


--
-- Data for Name: preliminary_plan; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.preliminary_plan (preliminary_plan_id, creation_timestamp, arranged_job_id) FROM stdin;
\.


--
-- Data for Name: preliminary_plan_employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.preliminary_plan_employee (preliminary_plan_employee_id, finish_date, is_height_work, needed_number, start_date, job_type_id, preliminary_plan_id, arranged_job_id) FROM stdin;
267	2019-03-15 01:00:00	f	1	2019-03-05 01:00:00	1	\N	\N
269	2019-03-14 01:00:00	f	1	2019-03-04 01:00:00	2	\N	\N
271	2019-03-16 01:00:00	f	10	2019-03-04 01:00:00	2	\N	\N
278	2019-03-21 01:00:00	f	4	2019-03-03 01:00:00	1	\N	226
\.


--
-- Data for Name: preliminary_plan_equipment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.preliminary_plan_equipment (preliminary_plan_equipment_id, finish_date, needed_number, start_date, equipment_type_id, preliminary_plan_id, arranged_job_id) FROM stdin;
268	2019-03-26 01:00:00	4	2019-03-03 01:00:00	1	\N	\N
273	2019-03-07 01:00:00	12	2019-03-08 01:00:00	1	\N	\N
277	2019-03-16 01:00:00	2	2019-03-03 01:00:00	2	\N	226
\.


--
-- Data for Name: preliminary_plan_mechanization; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.preliminary_plan_mechanization (preliminary_plan_mechanization_id, finish_date, needed_number, start_date, mechanization_group_id, mechanization_type_id, preliminary_plan_id, arranged_job_id) FROM stdin;
266	2019-03-13 01:00:00	12	2019-03-03 01:00:00	\N	2	\N	\N
270	2019-03-14 01:00:00	10	2019-03-04 01:00:00	\N	4	\N	\N
272	2019-03-05 01:00:00	14	2019-03-02 01:00:00	\N	11	\N	226
276	2019-03-15 01:00:00	5	2019-03-05 01:00:00	\N	14	\N	226
274	2019-03-12 01:00:00	5	2019-03-02 01:00:00	\N	2	\N	226
275	2019-03-06 01:00:00	5	2019-03-02 01:00:00	\N	12	\N	226
279	2019-03-19 01:00:00	1	2019-03-03 01:00:00	\N	13	\N	226
280	2019-03-27 01:00:00	124	2019-03-11 01:00:00	\N	5	\N	226
\.


--
-- Data for Name: real_estate; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.real_estate (real_estate_id, name, investor_id, location_id, address, city_id) FROM stdin;
25	Upravna zgrada 1	\N	\N	Kneza Miloša 11	1
28	Upravna zgrada 2	\N	\N	Vojvode Stepe bb	1
48	Fabrika	\N	\N	Batajnicki drum bb	1
80	Nova zgrada	\N	\N	Surčinski put bb	1
109	Fabrika	\N	\N	Petrovaradin	2
126	Upravna zgrada	\N	\N	Takovska 11	1
\.


--
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.role (role_id, name) FROM stdin;
1	KEY ACCOUNT
2	ADMINISTRATIVE SUPPORT
3	CHIEF PREPARATION
4	CHIEF USER\n
5	RESPOSIBLE USER
6	PROJECT MANAGER
7	MANAGER
8	HR
9	MR
10	ER
\.


--
-- Data for Name: sector; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sector (sector_id, name) FROM stdin;
1	GZR
2	AKZ
3	ELEKTRO
4	SUMA
5	Mehanizacija
6	Oprema
\.


--
-- Data for Name: user_jadran; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_jadran (user_id, firstname_lastname, password, phone, username, sector_id) FROM stdin;
6	Kristina AKZ	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Kristina	2
2	Igor Ilic	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Igor	2
3	Dragan Radicevic	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Dragan	3
4	Aleksandar Martinovic SUMA	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Aleksandar	4
5	Ksenija GZR	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Ksenija\n	1
7	Milan Protic ELEKTRO	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Milan	3
8	Gojko SUMA	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Gojko	4
9	Vlada PM GZR	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Vlada	1
11	Mirko ELEKTRO	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Mirko	3
10	Nenad PM AKZ	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Nenad	2
12	Matija SUMA	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Matija	4
13	Zobenica GZR	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	zobenica	1
14	Slavica AKZ	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	slavica	2
15	Marko Elektro	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	marko	3
16	Dragan Milosavljevic SUMA	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	dragan	4
17	\N	$2a$10$tz.lw1cm5Nkgu8HzYu3vGO/VSR3CT8IRpM23a6qhAUfp7F5Xy.5ju	\N	q	\N
18	\N	$2a$10$NMeBUtO1/OvRycUkx696Ouw1Fu.TrQErh9.wd66FvbpDSukPEa3qe	\N	q1	\N
19	\N	$2a$10$eVcSHTbsezjBSON47FFR4OpCSQBYTs6RqRuX8kDMT1QUtEaWrohEK	\N	q21	\N
1	Sanja Paukovic	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	Sanja	1
20	Zorica Tadić	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	zorica	\N
21	Ana Jovanivić	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	ana	\N
22	Nikola Klešić	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	nikola	\N
23	Miloš Kilibarda	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	milos	\N
24	Dušan Perić	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	dusan	\N
25	Vladimir Bakovic	$2a$10$eqlCUx/HNClPtFc.jQb1UevX3hK3d9bCFhwbZIii..3ERAWx8dl7a	\N	vladimir	1
\.


--
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_role (user_id, role_id) FROM stdin;
1	3
2	3
3	3
4	3
5	5
6	5
7	5
8	5
5	6
9	6
10	6
11	6
12	6
13	4
14	4
15	4
16	4
20	1
6	1
21	1
22	8
23	9
24	10
25	6
\.


--
-- Data for Name: work_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.work_type (work_type_id, name) FROM stdin;
3	Građevinski radovi
1	Moler
2	Električar
\.


--
-- Data for Name: worker_project_day; Type: TABLE DATA; Schema: public; Owner: nikola
--

COPY public.worker_project_day (date, time_of_arrival, time_of_departure, value, human_resource_id, arranged_job_id, sector_id) FROM stdin;
2019-03-01 19:20:25.799	\N	\N	1	1	226	\N
2019-03-02 19:20:25.799	\N	\N	1	1	226	\N
2019-03-03 19:20:25.799	\N	\N	0	1	226	\N
2019-03-04 19:20:25.799	\N	\N	0	1	226	\N
2019-03-05 19:20:25.799	\N	\N	0	1	226	\N
2019-03-06 19:20:25.799	\N	\N	0	1	226	\N
2019-03-07 19:20:25.799	\N	\N	0	1	226	\N
2019-03-08 19:20:25.799	\N	\N	0	1	226	\N
2019-03-09 19:20:25.799	\N	\N	0	1	226	\N
2019-03-10 19:20:25.799	\N	\N	0	1	226	\N
2019-03-11 19:20:25.799	\N	\N	0	1	226	\N
2019-03-12 19:20:25.799	\N	\N	0	1	226	\N
2019-03-13 19:20:25.799	\N	\N	0	1	226	\N
2019-03-14 19:20:25.799	\N	\N	0	1	226	\N
2019-03-15 19:20:25.799	\N	\N	0	1	226	\N
2019-03-16 19:20:25.799	\N	\N	0	1	226	\N
2019-03-17 19:20:25.799	\N	\N	0	1	226	\N
2019-03-18 19:20:25.799	\N	\N	0	1	226	\N
2019-03-19 19:20:25.799	\N	\N	0	1	226	\N
2019-03-20 19:20:25.799	\N	\N	0	1	226	\N
2019-03-21 19:20:25.799	\N	\N	0	1	226	\N
2019-03-22 19:20:25.799	\N	\N	0	1	226	\N
2019-03-23 19:20:25.799	\N	\N	0	1	226	\N
2019-03-24 19:20:25.799	\N	\N	0	1	226	\N
2019-03-25 19:20:25.799	\N	\N	0	1	226	\N
2019-03-26 19:20:25.799	\N	\N	0	1	226	\N
2019-03-27 19:20:25.799	\N	\N	0	1	226	\N
2019-03-28 19:20:25.799	\N	\N	0	1	226	\N
2019-03-29 19:20:25.799	\N	\N	0	1	226	\N
2019-03-30 19:20:25.799	\N	\N	0	1	226	\N
2019-03-31 19:20:25.799	\N	\N	0	1	226	\N
2019-04-01 19:20:25.799	\N	\N	0	1	226	\N
2019-04-02 19:20:25.799	\N	\N	0	1	226	\N
2019-04-03 19:20:25.799	\N	\N	0	1	226	\N
2019-04-04 19:20:25.799	\N	\N	0	1	226	\N
2019-04-05 19:20:25.799	\N	\N	0	1	226	\N
2019-04-06 19:20:25.799	\N	\N	0	1	226	\N
2019-04-07 19:20:25.799	\N	\N	0	1	226	\N
2019-04-08 19:20:25.799	\N	\N	0	1	226	\N
2019-04-09 19:20:25.799	\N	\N	0	1	226	\N
2019-04-10 19:20:25.799	\N	\N	0	1	226	\N
2019-04-11 19:20:25.799	\N	\N	0	1	226	\N
2019-04-12 19:20:25.799	\N	\N	0	1	226	\N
2019-04-13 19:20:25.799	\N	\N	0	1	226	\N
2019-04-14 19:20:25.799	\N	\N	0	1	226	\N
2019-04-15 19:20:25.799	\N	\N	0	1	226	\N
2019-04-16 19:20:25.799	\N	\N	0	1	226	\N
2019-04-17 19:20:25.799	\N	\N	0	1	226	\N
2019-04-18 19:20:25.799	\N	\N	0	1	226	\N
2019-04-19 19:20:25.799	\N	\N	0	1	226	\N
2019-04-20 19:20:25.799	\N	\N	0	1	226	\N
2019-04-21 19:20:25.799	\N	\N	0	1	226	\N
2019-04-22 19:20:25.799	\N	\N	0	1	226	\N
2019-04-23 19:20:25.799	\N	\N	0	1	226	\N
2019-04-24 19:20:25.799	\N	\N	0	1	226	\N
2019-04-25 19:20:25.799	\N	\N	0	1	226	\N
2019-04-26 19:20:25.799	\N	\N	0	1	226	\N
2019-04-27 19:20:25.799	\N	\N	0	1	226	\N
2019-04-28 19:20:25.799	\N	\N	0	1	226	\N
2019-04-29 19:20:25.799	\N	\N	0	1	226	\N
2019-04-30 19:20:25.799	\N	\N	0	1	226	\N
2019-05-01 19:20:25.799	\N	\N	0	1	226	\N
2019-05-02 19:20:25.799	\N	\N	0	1	226	\N
2019-05-03 19:20:25.799	\N	\N	0	1	226	\N
2019-05-04 19:20:25.799	\N	\N	0	1	226	\N
2019-05-05 19:20:25.799	\N	\N	0	1	226	\N
2019-05-06 19:20:25.799	\N	\N	0	1	226	\N
2019-05-07 19:20:25.799	\N	\N	0	1	226	\N
2019-05-08 19:20:25.799	\N	\N	0	1	226	\N
2019-05-09 19:20:25.799	\N	\N	0	1	226	\N
2019-05-10 19:20:25.799	\N	\N	0	1	226	\N
2019-05-11 19:20:25.799	\N	\N	0	1	226	\N
2019-05-12 19:20:25.799	\N	\N	0	1	226	\N
2019-05-13 19:20:25.799	\N	\N	0	1	226	\N
2019-05-14 19:20:25.799	\N	\N	0	1	226	\N
2019-05-15 19:20:25.799	\N	\N	0	1	226	\N
2019-05-16 19:20:25.799	\N	\N	0	1	226	\N
2019-05-17 19:20:25.799	\N	\N	0	1	226	\N
2019-05-18 19:20:25.799	\N	\N	0	1	226	\N
2019-05-19 19:20:25.799	\N	\N	0	1	226	\N
2019-05-20 19:20:25.799	\N	\N	0	1	226	\N
2019-05-21 19:20:25.799	\N	\N	0	1	226	\N
2019-05-22 19:20:25.799	\N	\N	0	1	226	\N
2019-05-23 19:20:25.799	\N	\N	0	1	226	\N
2019-05-24 19:20:25.799	\N	\N	0	1	226	\N
2019-05-25 19:20:25.799	\N	\N	0	1	226	\N
2019-05-26 19:20:25.799	\N	\N	0	1	226	\N
2019-05-27 19:20:25.799	\N	\N	0	1	226	\N
2019-05-28 19:20:25.799	\N	\N	0	1	226	\N
2019-05-29 19:20:25.799	\N	\N	0	1	226	\N
2019-05-30 19:20:25.799	\N	\N	0	1	226	\N
2019-05-31 19:20:25.799	\N	\N	0	1	226	\N
2019-06-01 19:20:25.799	\N	\N	0	1	226	\N
2019-06-02 19:20:25.799	\N	\N	0	1	226	\N
2019-06-03 19:20:25.799	\N	\N	0	1	226	\N
2019-06-04 19:20:25.799	\N	\N	0	1	226	\N
2019-06-05 19:20:25.799	\N	\N	0	1	226	\N
2019-06-06 19:20:25.799	\N	\N	0	1	226	\N
2019-06-07 19:20:25.799	\N	\N	0	1	226	\N
2019-06-08 19:20:25.799	\N	\N	0	1	226	\N
2019-06-09 19:20:25.799	\N	\N	0	1	226	\N
2019-06-10 19:20:25.799	\N	\N	0	1	226	\N
2019-06-11 19:20:25.799	\N	\N	0	1	226	\N
2019-06-12 19:20:25.799	\N	\N	0	1	226	\N
2019-06-13 19:20:25.799	\N	\N	0	1	226	\N
2019-06-14 19:20:25.799	\N	\N	0	1	226	\N
2019-06-15 19:20:25.799	\N	\N	0	1	226	\N
2019-06-16 19:20:25.799	\N	\N	0	1	226	\N
2019-06-17 19:20:25.799	\N	\N	0	1	226	\N
2019-06-18 19:20:25.799	\N	\N	0	1	226	\N
2019-06-19 19:20:25.799	\N	\N	0	1	226	\N
2019-06-20 19:20:25.799	\N	\N	0	1	226	\N
2019-06-21 19:20:25.799	\N	\N	0	1	226	\N
2019-06-22 19:20:25.799	\N	\N	0	1	226	\N
2019-06-23 19:20:25.799	\N	\N	0	1	226	\N
2019-06-24 19:20:25.799	\N	\N	0	1	226	\N
2019-06-25 19:20:25.799	\N	\N	0	1	226	\N
2019-06-26 19:20:25.799	\N	\N	0	1	226	\N
2019-06-27 19:20:25.799	\N	\N	0	1	226	\N
2019-06-28 19:20:25.799	\N	\N	0	1	226	\N
2019-03-01 19:20:39.454	\N	\N	1	1	226	\N
2019-03-02 19:20:39.454	\N	\N	1	1	226	\N
2019-03-03 19:20:39.454	\N	\N	0	1	226	\N
2019-03-04 19:20:39.454	\N	\N	0	1	226	\N
2019-03-05 19:20:39.454	\N	\N	0	1	226	\N
2019-03-06 19:20:39.454	\N	\N	0	1	226	\N
2019-03-07 19:20:39.454	\N	\N	0	1	226	\N
2019-03-08 19:20:39.454	\N	\N	0	1	226	\N
2019-03-09 19:20:39.454	\N	\N	0	1	226	\N
2019-03-10 19:20:39.454	\N	\N	0	1	226	\N
2019-03-11 19:20:39.454	\N	\N	0	1	226	\N
2019-03-12 19:20:39.454	\N	\N	0	1	226	\N
2019-03-13 19:20:39.454	\N	\N	0	1	226	\N
2019-03-14 19:20:39.454	\N	\N	0	1	226	\N
2019-03-15 19:20:39.454	\N	\N	0	1	226	\N
2019-03-16 19:20:39.454	\N	\N	0	1	226	\N
2019-03-17 19:20:39.454	\N	\N	0	1	226	\N
2019-03-18 19:20:39.454	\N	\N	0	1	226	\N
2019-03-19 19:20:39.454	\N	\N	0	1	226	\N
2019-03-20 19:20:39.454	\N	\N	0	1	226	\N
2019-03-21 19:20:39.454	\N	\N	0	1	226	\N
2019-03-22 19:20:39.454	\N	\N	0	1	226	\N
2019-03-23 19:20:39.454	\N	\N	0	1	226	\N
2019-03-24 19:20:39.454	\N	\N	0	1	226	\N
2019-03-25 19:20:39.454	\N	\N	0	1	226	\N
2019-03-26 19:20:39.454	\N	\N	0	1	226	\N
2019-03-27 19:20:39.454	\N	\N	0	1	226	\N
2019-03-28 19:20:39.454	\N	\N	0	1	226	\N
2019-03-29 19:20:39.454	\N	\N	0	1	226	\N
2019-03-30 19:20:39.454	\N	\N	0	1	226	\N
2019-03-31 19:20:39.454	\N	\N	0	1	226	\N
2019-04-01 19:20:39.454	\N	\N	0	1	226	\N
2019-04-02 19:20:39.454	\N	\N	0	1	226	\N
2019-04-03 19:20:39.454	\N	\N	0	1	226	\N
2019-04-04 19:20:39.454	\N	\N	0	1	226	\N
2019-04-05 19:20:39.454	\N	\N	0	1	226	\N
2019-04-06 19:20:39.454	\N	\N	0	1	226	\N
2019-04-07 19:20:39.454	\N	\N	0	1	226	\N
2019-04-08 19:20:39.454	\N	\N	0	1	226	\N
2019-04-09 19:20:39.454	\N	\N	0	1	226	\N
2019-04-10 19:20:39.454	\N	\N	0	1	226	\N
2019-04-11 19:20:39.454	\N	\N	0	1	226	\N
2019-04-12 19:20:39.454	\N	\N	0	1	226	\N
2019-04-13 19:20:39.454	\N	\N	0	1	226	\N
2019-04-14 19:20:39.454	\N	\N	0	1	226	\N
2019-04-15 19:20:39.454	\N	\N	0	1	226	\N
2019-04-16 19:20:39.454	\N	\N	0	1	226	\N
2019-04-17 19:20:39.454	\N	\N	0	1	226	\N
2019-04-18 19:20:39.454	\N	\N	0	1	226	\N
2019-04-19 19:20:39.454	\N	\N	0	1	226	\N
2019-04-20 19:20:39.454	\N	\N	0	1	226	\N
2019-04-21 19:20:39.454	\N	\N	0	1	226	\N
2019-04-22 19:20:39.454	\N	\N	0	1	226	\N
2019-04-23 19:20:39.454	\N	\N	0	1	226	\N
2019-04-24 19:20:39.454	\N	\N	0	1	226	\N
2019-04-25 19:20:39.454	\N	\N	0	1	226	\N
2019-04-26 19:20:39.454	\N	\N	0	1	226	\N
2019-04-27 19:20:39.454	\N	\N	0	1	226	\N
2019-04-28 19:20:39.454	\N	\N	0	1	226	\N
2019-04-29 19:20:39.454	\N	\N	0	1	226	\N
2019-04-30 19:20:39.454	\N	\N	0	1	226	\N
2019-05-01 19:20:39.454	\N	\N	0	1	226	\N
2019-05-02 19:20:39.454	\N	\N	0	1	226	\N
2019-05-03 19:20:39.454	\N	\N	0	1	226	\N
2019-05-04 19:20:39.454	\N	\N	0	1	226	\N
2019-05-05 19:20:39.454	\N	\N	0	1	226	\N
2019-05-06 19:20:39.454	\N	\N	0	1	226	\N
2019-05-07 19:20:39.454	\N	\N	0	1	226	\N
2019-05-08 19:20:39.454	\N	\N	0	1	226	\N
2019-05-09 19:20:39.454	\N	\N	0	1	226	\N
2019-05-10 19:20:39.454	\N	\N	0	1	226	\N
2019-05-11 19:20:39.454	\N	\N	0	1	226	\N
2019-05-12 19:20:39.454	\N	\N	0	1	226	\N
2019-05-13 19:20:39.454	\N	\N	0	1	226	\N
2019-05-14 19:20:39.454	\N	\N	0	1	226	\N
2019-05-15 19:20:39.454	\N	\N	0	1	226	\N
2019-05-16 19:20:39.454	\N	\N	0	1	226	\N
2019-05-17 19:20:39.454	\N	\N	0	1	226	\N
2019-05-18 19:20:39.454	\N	\N	0	1	226	\N
2019-05-19 19:20:39.454	\N	\N	0	1	226	\N
2019-05-20 19:20:39.454	\N	\N	0	1	226	\N
2019-05-21 19:20:39.454	\N	\N	0	1	226	\N
2019-05-22 19:20:39.454	\N	\N	0	1	226	\N
2019-05-23 19:20:39.454	\N	\N	0	1	226	\N
2019-05-24 19:20:39.454	\N	\N	0	1	226	\N
2019-05-25 19:20:39.454	\N	\N	0	1	226	\N
2019-05-26 19:20:39.454	\N	\N	0	1	226	\N
2019-05-27 19:20:39.454	\N	\N	0	1	226	\N
2019-05-28 19:20:39.454	\N	\N	0	1	226	\N
2019-05-29 19:20:39.454	\N	\N	0	1	226	\N
2019-05-30 19:20:39.454	\N	\N	0	1	226	\N
2019-05-31 19:20:39.454	\N	\N	0	1	226	\N
2019-06-01 19:20:39.454	\N	\N	0	1	226	\N
2019-06-02 19:20:39.454	\N	\N	0	1	226	\N
2019-06-03 19:20:39.454	\N	\N	0	1	226	\N
2019-06-04 19:20:39.454	\N	\N	0	1	226	\N
2019-06-05 19:20:39.454	\N	\N	0	1	226	\N
2019-06-06 19:20:39.454	\N	\N	0	1	226	\N
2019-06-07 19:20:39.454	\N	\N	0	1	226	\N
2019-06-08 19:20:39.454	\N	\N	0	1	226	\N
2019-06-09 19:20:39.454	\N	\N	0	1	226	\N
2019-06-10 19:20:39.454	\N	\N	0	1	226	\N
2019-06-11 19:20:39.454	\N	\N	0	1	226	\N
2019-06-12 19:20:39.454	\N	\N	0	1	226	\N
2019-06-13 19:20:39.454	\N	\N	0	1	226	\N
2019-06-14 19:20:39.454	\N	\N	0	1	226	\N
2019-06-15 19:20:39.454	\N	\N	0	1	226	\N
2019-06-16 19:20:39.454	\N	\N	0	1	226	\N
2019-06-17 19:20:39.454	\N	\N	0	1	226	\N
2019-06-18 19:20:39.454	\N	\N	0	1	226	\N
2019-06-19 19:20:39.454	\N	\N	0	1	226	\N
2019-06-20 19:20:39.454	\N	\N	0	1	226	\N
2019-06-21 19:20:39.454	\N	\N	0	1	226	\N
2019-06-22 19:20:39.454	\N	\N	0	1	226	\N
2019-06-23 19:20:39.454	\N	\N	0	1	226	\N
2019-06-24 19:20:39.454	\N	\N	0	1	226	\N
2019-06-25 19:20:39.454	\N	\N	0	1	226	\N
2019-06-26 19:20:39.454	\N	\N	0	1	226	\N
2019-06-27 19:20:39.454	\N	\N	0	1	226	\N
2019-06-28 19:20:39.454	\N	\N	0	1	226	\N
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 280, true);


--
-- Name: annex annex_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annex
    ADD CONSTRAINT annex_pkey PRIMARY KEY (annex_id);


--
-- Name: area_of_expertise area_of_expertise_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.area_of_expertise
    ADD CONSTRAINT area_of_expertise_pkey PRIMARY KEY (area_of_expertise_id);


--
-- Name: arranged_job_delay arranged_job_delay_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job_delay
    ADD CONSTRAINT arranged_job_delay_pkey PRIMARY KEY (arranged_job_delay_id);


--
-- Name: arranged_job arranged_job_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job
    ADD CONSTRAINT arranged_job_pkey PRIMARY KEY (arranged_job_id);


--
-- Name: bank_account bank_account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bank_account
    ADD CONSTRAINT bank_account_pkey PRIMARY KEY (bank_account_id);


--
-- Name: cache cache_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cache
    ADD CONSTRAINT cache_pkey PRIMARY KEY (cache_id);


--
-- Name: calculant calculant_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.calculant
    ADD CONSTRAINT calculant_pkey PRIMARY KEY (calculant_id);


--
-- Name: city city_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (city_id);


--
-- Name: construction_insurance construction_insurance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.construction_insurance
    ADD CONSTRAINT construction_insurance_pkey PRIMARY KEY (construction_insurance_id);


--
-- Name: contact contact_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contact
    ADD CONSTRAINT contact_pkey PRIMARY KEY (contact_id);


--
-- Name: contract contract_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT contract_pkey PRIMARY KEY (contract_id);


--
-- Name: contract_type contract_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract_type
    ADD CONSTRAINT contract_type_pkey PRIMARY KEY (contract_type_id);


--
-- Name: contractor_area_of_expertise contractor_area_of_expertise_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_area_of_expertise
    ADD CONSTRAINT contractor_area_of_expertise_pkey PRIMARY KEY (area_of_expertise_id, contractor_id);


--
-- Name: contractor_bank_account contractor_bank_account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_bank_account
    ADD CONSTRAINT contractor_bank_account_pkey PRIMARY KEY (contractor_bank_account_id);


--
-- Name: contractor_contact contractor_contact_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_contact
    ADD CONSTRAINT contractor_contact_pkey PRIMARY KEY (contractor_contact_id);


--
-- Name: contractor contractor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor
    ADD CONSTRAINT contractor_pkey PRIMARY KEY (contractor_id);


--
-- Name: contractor_work_type contractor_work_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_work_type
    ADD CONSTRAINT contractor_work_type_pkey PRIMARY KEY (contractor_id, work_type_id);


--
-- Name: employer employer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employer
    ADD CONSTRAINT employer_pkey PRIMARY KEY (employer_id);


--
-- Name: equipment equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipment
    ADD CONSTRAINT equipment_pkey PRIMARY KEY (equipment_id);


--
-- Name: equipment_project_day equipment_project_day_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipment_project_day
    ADD CONSTRAINT equipment_project_day_pkey PRIMARY KEY (arranged_job_id, date, equipment_id);


--
-- Name: equipment_type equipment_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipment_type
    ADD CONSTRAINT equipment_type_pkey PRIMARY KEY (equipment_type_id);


--
-- Name: financial_instrument financial_instrument_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.financial_instrument
    ADD CONSTRAINT financial_instrument_pkey PRIMARY KEY (financial_instrument_id);


--
-- Name: financial_security_asset financial_security_asset_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.financial_security_asset
    ADD CONSTRAINT financial_security_asset_pkey PRIMARY KEY (financial_security_asset_id);


--
-- Name: human_resource human_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.human_resource
    ADD CONSTRAINT human_resource_pkey PRIMARY KEY (human_resource_id);


--
-- Name: investor_bank_accounts investor_bank_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_bank_accounts
    ADD CONSTRAINT investor_bank_accounts_pkey PRIMARY KEY (investor_investor_id, bank_accounts_bank_account_id);


--
-- Name: investor_contacts investor_contacts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_contacts
    ADD CONSTRAINT investor_contacts_pkey PRIMARY KEY (investor_investor_id, contacts_contact_id);


--
-- Name: investor investor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor
    ADD CONSTRAINT investor_pkey PRIMARY KEY (investor_id);


--
-- Name: investor_real_estates investor_real_estates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_real_estates
    ADD CONSTRAINT investor_real_estates_pkey PRIMARY KEY (investor_investor_id, real_estates_real_estate_id);


--
-- Name: invoice invoice_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice
    ADD CONSTRAINT invoice_pkey PRIMARY KEY (invoice_id);


--
-- Name: job_type job_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.job_type
    ADD CONSTRAINT job_type_pkey PRIMARY KEY (job_type_id);


--
-- Name: location location_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.location
    ADD CONSTRAINT location_pkey PRIMARY KEY (location_id);


--
-- Name: mechanization_group mechanization_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization_group
    ADD CONSTRAINT mechanization_group_pkey PRIMARY KEY (mechanization_group_id);


--
-- Name: mechanization mechanization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization
    ADD CONSTRAINT mechanization_pkey PRIMARY KEY (mechanization_id);


--
-- Name: mechanization_project_day mechanization_project_day_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization_project_day
    ADD CONSTRAINT mechanization_project_day_pkey PRIMARY KEY (arranged_job_id, date, mechanization_id);


--
-- Name: mechanization_type mechanization_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization_type
    ADD CONSTRAINT mechanization_type_pkey PRIMARY KEY (mechanization_type_id);


--
-- Name: offer_financial_security_asset offer_financial_security_asset_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer_financial_security_asset
    ADD CONSTRAINT offer_financial_security_asset_pkey PRIMARY KEY (financial_security_asset_id, offer_id);


--
-- Name: offer offer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT offer_pkey PRIMARY KEY (offer_id);


--
-- Name: offer_type offer_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer_type
    ADD CONSTRAINT offer_type_pkey PRIMARY KEY (offer_type_id);


--
-- Name: phase_contractor phase_contractor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phase_contractor
    ADD CONSTRAINT phase_contractor_pkey PRIMARY KEY (contractor_id, phase_id);


--
-- Name: phase phase_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phase
    ADD CONSTRAINT phase_pkey PRIMARY KEY (phase_id);


--
-- Name: preliminary_plan_employee preliminary_plan_employee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_employee
    ADD CONSTRAINT preliminary_plan_employee_pkey PRIMARY KEY (preliminary_plan_employee_id);


--
-- Name: preliminary_plan_equipment preliminary_plan_equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_equipment
    ADD CONSTRAINT preliminary_plan_equipment_pkey PRIMARY KEY (preliminary_plan_equipment_id);


--
-- Name: preliminary_plan_mechanization preliminary_plan_mechanization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_mechanization
    ADD CONSTRAINT preliminary_plan_mechanization_pkey PRIMARY KEY (preliminary_plan_mechanization_id);


--
-- Name: preliminary_plan preliminary_plan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan
    ADD CONSTRAINT preliminary_plan_pkey PRIMARY KEY (preliminary_plan_id);


--
-- Name: real_estate real_estate_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.real_estate
    ADD CONSTRAINT real_estate_pkey PRIMARY KEY (real_estate_id);


--
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- Name: sector sector_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sector
    ADD CONSTRAINT sector_pkey PRIMARY KEY (sector_id);


--
-- Name: investor uk_6vswtqju1dyetev6lsgwxmufs; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor
    ADD CONSTRAINT uk_6vswtqju1dyetev6lsgwxmufs UNIQUE (investor_number);


--
-- Name: investor_bank_accounts uk_7pmq0gtd6wlanasxfrqoich9d; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_bank_accounts
    ADD CONSTRAINT uk_7pmq0gtd6wlanasxfrqoich9d UNIQUE (bank_accounts_bank_account_id);


--
-- Name: investor_real_estates uk_8s23lrn6bgm43y9l2a296uc2w; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_real_estates
    ADD CONSTRAINT uk_8s23lrn6bgm43y9l2a296uc2w UNIQUE (real_estates_real_estate_id);


--
-- Name: investor_contacts uk_hy9ud3lo2jvq8mofxbd6yxde3; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_contacts
    ADD CONSTRAINT uk_hy9ud3lo2jvq8mofxbd6yxde3 UNIQUE (contacts_contact_id);


--
-- Name: contract uk_kllucnx5g9ohgekr8c3ttmr8d; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT uk_kllucnx5g9ohgekr8c3ttmr8d UNIQUE (contract_number);


--
-- Name: user_jadran user_jadran_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_jadran
    ADD CONSTRAINT user_jadran_pkey PRIMARY KEY (user_id);


--
-- Name: user_role user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: work_type work_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.work_type
    ADD CONSTRAINT work_type_pkey PRIMARY KEY (work_type_id);


--
-- Name: worker_project_day worker_project_day_pkey; Type: CONSTRAINT; Schema: public; Owner: nikola
--

ALTER TABLE ONLY public.worker_project_day
    ADD CONSTRAINT worker_project_day_pkey PRIMARY KEY (arranged_job_id, date, human_resource_id);


--
-- Name: mechanization_project_day fk12fa7ky5y3m6own4smvgr1a0n; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization_project_day
    ADD CONSTRAINT fk12fa7ky5y3m6own4smvgr1a0n FOREIGN KEY (chief_user_id) REFERENCES public.user_jadran(user_id);


--
-- Name: investor_bank_accounts fk1kw46m3nm7n3hu0r52vfwklgl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_bank_accounts
    ADD CONSTRAINT fk1kw46m3nm7n3hu0r52vfwklgl FOREIGN KEY (bank_accounts_bank_account_id) REFERENCES public.bank_account(bank_account_id);


--
-- Name: offer fk2atukmnaruo8cfaainwmimp4g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fk2atukmnaruo8cfaainwmimp4g FOREIGN KEY (sector_id) REFERENCES public.sector(sector_id);


--
-- Name: phase_contractor fk2ecirtewbc2ritfpqcex0mpry; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phase_contractor
    ADD CONSTRAINT fk2ecirtewbc2ritfpqcex0mpry FOREIGN KEY (area_of_expertise_id) REFERENCES public.area_of_expertise(area_of_expertise_id);


--
-- Name: arranged_job_delay fk2ir1rsuuo58gs2a6wt33nbsl9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job_delay
    ADD CONSTRAINT fk2ir1rsuuo58gs2a6wt33nbsl9 FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: investor_bank_accounts fk2pco9wkibstvkrlbu99hclu0o; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_bank_accounts
    ADD CONSTRAINT fk2pco9wkibstvkrlbu99hclu0o FOREIGN KEY (investor_investor_id) REFERENCES public.investor(investor_id);


--
-- Name: contractor_contact fk2qw8k92t4vdxa7oxlw1oyiqg5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_contact
    ADD CONSTRAINT fk2qw8k92t4vdxa7oxlw1oyiqg5 FOREIGN KEY (contractor_id) REFERENCES public.contractor(contractor_id);


--
-- Name: arranged_job fk2tjyk84rm5au2516ebfg0qx98; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job
    ADD CONSTRAINT fk2tjyk84rm5au2516ebfg0qx98 FOREIGN KEY (project_manager_user_id) REFERENCES public.user_jadran(user_id);


--
-- Name: mechanization fk31g6p3xsi7nwd6yb445duhy7y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization
    ADD CONSTRAINT fk31g6p3xsi7nwd6yb445duhy7y FOREIGN KEY (mechanization_group_id) REFERENCES public.mechanization_group(mechanization_group_id);


--
-- Name: invoice fk3weftywngjgoqf3ta6y07u8wi; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoice
    ADD CONSTRAINT fk3weftywngjgoqf3ta6y07u8wi FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: contractor_work_type fk42yru8wu3lybwgrccsokeieph; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_work_type
    ADD CONSTRAINT fk42yru8wu3lybwgrccsokeieph FOREIGN KEY (work_type_id) REFERENCES public.work_type(work_type_id);


--
-- Name: offer_financial_security_asset fk4b8rjn2f4m8pevi1xfa50edpk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer_financial_security_asset
    ADD CONSTRAINT fk4b8rjn2f4m8pevi1xfa50edpk FOREIGN KEY (financial_security_asset_id) REFERENCES public.financial_security_asset(financial_security_asset_id);


--
-- Name: contractor_area_of_expertise fk4sei9dmakfbcc7fj9bker5sf8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_area_of_expertise
    ADD CONSTRAINT fk4sei9dmakfbcc7fj9bker5sf8 FOREIGN KEY (contractor_id) REFERENCES public.contractor(contractor_id);


--
-- Name: worker_project_day fk4w9wqjotlinqpu540otrcxms3; Type: FK CONSTRAINT; Schema: public; Owner: nikola
--

ALTER TABLE ONLY public.worker_project_day
    ADD CONSTRAINT fk4w9wqjotlinqpu540otrcxms3 FOREIGN KEY (sector_id) REFERENCES public.sector(sector_id);


--
-- Name: phase fk564uvlf62xisr295dd4ojnb0c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phase
    ADD CONSTRAINT fk564uvlf62xisr295dd4ojnb0c FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: offer fk57d2lrvs8dvw217gvxa4foi9a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fk57d2lrvs8dvw217gvxa4foi9a FOREIGN KEY (contact_id) REFERENCES public.contact(contact_id);


--
-- Name: preliminary_plan_employee fk584vaj2bkq0oqgvxotegdjx2m; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_employee
    ADD CONSTRAINT fk584vaj2bkq0oqgvxotegdjx2m FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: location fk5xh52u52pew9nq1hkoiqnyc4t; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.location
    ADD CONSTRAINT fk5xh52u52pew9nq1hkoiqnyc4t FOREIGN KEY (real_estate_id) REFERENCES public.real_estate(real_estate_id);


--
-- Name: equipment_project_day fk66yhrrd21lgbkcjgtdy0eoejf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipment_project_day
    ADD CONSTRAINT fk66yhrrd21lgbkcjgtdy0eoejf FOREIGN KEY (sector_id) REFERENCES public.sector(sector_id);


--
-- Name: offer fk6aha9dhd9dh5funpdv2vrd8y1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fk6aha9dhd9dh5funpdv2vrd8y1 FOREIGN KEY (investor_id) REFERENCES public.investor(investor_id);


--
-- Name: human_resource fk6nereq40dw4xv40gko9gklbib; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.human_resource
    ADD CONSTRAINT fk6nereq40dw4xv40gko9gklbib FOREIGN KEY (employer_id) REFERENCES public.employer(employer_id);


--
-- Name: contractor_work_type fk7eim1p903k515e4nyk4xxwsaw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_work_type
    ADD CONSTRAINT fk7eim1p903k515e4nyk4xxwsaw FOREIGN KEY (contractor_id) REFERENCES public.contractor(contractor_id);


--
-- Name: mechanization_project_day fk7lf367o6jdvtt9oxje8um1s3q; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization_project_day
    ADD CONSTRAINT fk7lf367o6jdvtt9oxje8um1s3q FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: contractor_area_of_expertise fk7yekl001bkug473fajcxmf3rk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_area_of_expertise
    ADD CONSTRAINT fk7yekl001bkug473fajcxmf3rk FOREIGN KEY (area_of_expertise_id) REFERENCES public.area_of_expertise(area_of_expertise_id);


--
-- Name: human_resource fk8bsx2p4aeqownu7tfk1v56erb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.human_resource
    ADD CONSTRAINT fk8bsx2p4aeqownu7tfk1v56erb FOREIGN KEY (city_id) REFERENCES public.city(city_id);


--
-- Name: preliminary_plan_employee fk8e78uew86bxv0p259c86r91td; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_employee
    ADD CONSTRAINT fk8e78uew86bxv0p259c86r91td FOREIGN KEY (job_type_id) REFERENCES public.job_type(job_type_id);


--
-- Name: real_estate fk8hmuuamnwju480ydde3lxwm5e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.real_estate
    ADD CONSTRAINT fk8hmuuamnwju480ydde3lxwm5e FOREIGN KEY (city_id) REFERENCES public.city(city_id);


--
-- Name: preliminary_plan_equipment fk8p2kg2v0wlfoqmrw495tuwoif; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_equipment
    ADD CONSTRAINT fk8p2kg2v0wlfoqmrw495tuwoif FOREIGN KEY (equipment_type_id) REFERENCES public.equipment_type(equipment_type_id);


--
-- Name: investor_contacts fk8t0pplhadwr2q1aboaqswo8u7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_contacts
    ADD CONSTRAINT fk8t0pplhadwr2q1aboaqswo8u7 FOREIGN KEY (contacts_contact_id) REFERENCES public.contact(contact_id);


--
-- Name: offer_financial_security_asset fk98fpu3ukqep78pupd0h7newu4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer_financial_security_asset
    ADD CONSTRAINT fk98fpu3ukqep78pupd0h7newu4 FOREIGN KEY (offer_id) REFERENCES public.offer(offer_id);


--
-- Name: equipment_project_day fk9982p1d2y4rneol3xd3pfw6tw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipment_project_day
    ADD CONSTRAINT fk9982p1d2y4rneol3xd3pfw6tw FOREIGN KEY (equipment_id) REFERENCES public.equipment(equipment_id);


--
-- Name: investor_real_estates fk9iwagc8nq0oi350gpy63spcpn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_real_estates
    ADD CONSTRAINT fk9iwagc8nq0oi350gpy63spcpn FOREIGN KEY (investor_investor_id) REFERENCES public.investor(investor_id);


--
-- Name: user_role fka68196081fvovjhkek5m97n3y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES public.role(role_id);


--
-- Name: offer fkanvinb3hvh5ec9os88r66yw5w; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fkanvinb3hvh5ec9os88r66yw5w FOREIGN KEY (offer_type_id) REFERENCES public.offer_type(offer_type_id);


--
-- Name: preliminary_plan_equipment fkb3d9x5yl48xv3o5ft8f1g1p33; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_equipment
    ADD CONSTRAINT fkb3d9x5yl48xv3o5ft8f1g1p33 FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: offer fkbej9pxh7quv09swaskmf4t94f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fkbej9pxh7quv09swaskmf4t94f FOREIGN KEY (creator_user_id) REFERENCES public.user_jadran(user_id);


--
-- Name: real_estate fkcgwbn9xtxsfa4v91u3tbtvuqb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.real_estate
    ADD CONSTRAINT fkcgwbn9xtxsfa4v91u3tbtvuqb FOREIGN KEY (location_id) REFERENCES public.location(location_id);


--
-- Name: user_role fkd19k7tskppoi7n2a9xh4p7yq5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT fkd19k7tskppoi7n2a9xh4p7yq5 FOREIGN KEY (user_id) REFERENCES public.user_jadran(user_id);


--
-- Name: offer fkd562l3nm7odje0f3rkxqdsrpx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fkd562l3nm7odje0f3rkxqdsrpx FOREIGN KEY (calculant_id) REFERENCES public.calculant(calculant_id);


--
-- Name: equipment_project_day fkdpecw7upof9wkyunsr8sk14my; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipment_project_day
    ADD CONSTRAINT fkdpecw7upof9wkyunsr8sk14my FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: investor fke78uw5e438uk0mthvce5ui9w2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor
    ADD CONSTRAINT fke78uw5e438uk0mthvce5ui9w2 FOREIGN KEY (city_id) REFERENCES public.city(city_id);


--
-- Name: mechanization_project_day fkffbrjkctb0t5wi9pva1gusl4w; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization_project_day
    ADD CONSTRAINT fkffbrjkctb0t5wi9pva1gusl4w FOREIGN KEY (sector_id) REFERENCES public.sector(sector_id);


--
-- Name: contract fkfqso7ghamjcde51l47nhuger9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT fkfqso7ghamjcde51l47nhuger9 FOREIGN KEY (person_responsible_user_id) REFERENCES public.user_jadran(user_id);


--
-- Name: investor_contacts fkfyl5t34sv632ub6fral4u1ji7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_contacts
    ADD CONSTRAINT fkfyl5t34sv632ub6fral4u1ji7 FOREIGN KEY (investor_investor_id) REFERENCES public.investor(investor_id);


--
-- Name: preliminary_plan_employee fkg6gb9yr5tqpbwk2wo47mvsb0u; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_employee
    ADD CONSTRAINT fkg6gb9yr5tqpbwk2wo47mvsb0u FOREIGN KEY (preliminary_plan_id) REFERENCES public.preliminary_plan(preliminary_plan_id);


--
-- Name: preliminary_plan fkg9jhygirksqdr5ns2ieg6lcx0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan
    ADD CONSTRAINT fkg9jhygirksqdr5ns2ieg6lcx0 FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: offer fkgibtqv3nyjdi0e5p73xanc0e0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fkgibtqv3nyjdi0e5p73xanc0e0 FOREIGN KEY (user_id_who_rejected) REFERENCES public.user_jadran(user_id);


--
-- Name: preliminary_plan_mechanization fkgms180b9xvn5fd3dv7wt4g075; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_mechanization
    ADD CONSTRAINT fkgms180b9xvn5fd3dv7wt4g075 FOREIGN KEY (preliminary_plan_id) REFERENCES public.preliminary_plan(preliminary_plan_id);


--
-- Name: preliminary_plan_mechanization fkgxjhwsg56dgiagc0i8huwig4s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_mechanization
    ADD CONSTRAINT fkgxjhwsg56dgiagc0i8huwig4s FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: mechanization fkiwhvp0wf2s98rpcolbi5nts8c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization
    ADD CONSTRAINT fkiwhvp0wf2s98rpcolbi5nts8c FOREIGN KEY (mechanization_type_id) REFERENCES public.mechanization_type(mechanization_type_id);


--
-- Name: mechanization_type fkjpw4ko3sv37108fk6civ67ooj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization_type
    ADD CONSTRAINT fkjpw4ko3sv37108fk6civ67ooj FOREIGN KEY (mechanization_group_id) REFERENCES public.mechanization_group(mechanization_group_id);


--
-- Name: worker_project_day fkk6344p7ys08vyqj16lkk918gt; Type: FK CONSTRAINT; Schema: public; Owner: nikola
--

ALTER TABLE ONLY public.worker_project_day
    ADD CONSTRAINT fkk6344p7ys08vyqj16lkk918gt FOREIGN KEY (arranged_job_id) REFERENCES public.arranged_job(arranged_job_id);


--
-- Name: bank_account fkk6qxussssqgl95c5tfa0id1sr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bank_account
    ADD CONSTRAINT fkk6qxussssqgl95c5tfa0id1sr FOREIGN KEY (investor_id) REFERENCES public.investor(investor_id);


--
-- Name: preliminary_plan_mechanization fkk77ndystpfwrqnnbd5w2et9yl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_mechanization
    ADD CONSTRAINT fkk77ndystpfwrqnnbd5w2et9yl FOREIGN KEY (mechanization_group_id) REFERENCES public.mechanization_group(mechanization_group_id);


--
-- Name: phase_contractor fkkruv9t01k2gcth16982psnayl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phase_contractor
    ADD CONSTRAINT fkkruv9t01k2gcth16982psnayl FOREIGN KEY (phase_id) REFERENCES public.phase(phase_id);


--
-- Name: contract fkktupo8f38d16yo4kbd1vj7l8b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT fkktupo8f38d16yo4kbd1vj7l8b FOREIGN KEY (offer_id) REFERENCES public.offer(offer_id);


--
-- Name: phase_contractor fkku5cgn88ixi6esa93qnxymsfn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phase_contractor
    ADD CONSTRAINT fkku5cgn88ixi6esa93qnxymsfn FOREIGN KEY (contractor_id) REFERENCES public.contractor(contractor_id);


--
-- Name: construction_insurance fkl07ifp5v7ad8lhd2cwndjasbn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.construction_insurance
    ADD CONSTRAINT fkl07ifp5v7ad8lhd2cwndjasbn FOREIGN KEY (offer_id) REFERENCES public.offer(offer_id);


--
-- Name: offer fkl3ps6cl7n8vpmcvgno8urgaek; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fkl3ps6cl7n8vpmcvgno8urgaek FOREIGN KEY (contract_type_id) REFERENCES public.contract_type(contract_type_id);


--
-- Name: arranged_job fkls70hap0l3xl6thaip9anoj0m; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job
    ADD CONSTRAINT fkls70hap0l3xl6thaip9anoj0m FOREIGN KEY (location_id) REFERENCES public.location(location_id);


--
-- Name: contact fkm1vk2uq92ylut7jv0mli2stop; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contact
    ADD CONSTRAINT fkm1vk2uq92ylut7jv0mli2stop FOREIGN KEY (investor_id) REFERENCES public.investor(investor_id);


--
-- Name: equipment_project_day fkm5he4mvkwnoelm6yaomsdk24k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipment_project_day
    ADD CONSTRAINT fkm5he4mvkwnoelm6yaomsdk24k FOREIGN KEY (chief_user_id) REFERENCES public.user_jadran(user_id);


--
-- Name: real_estate fknrrhegqww77vpepui2l01q3qk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.real_estate
    ADD CONSTRAINT fknrrhegqww77vpepui2l01q3qk FOREIGN KEY (investor_id) REFERENCES public.investor(investor_id);


--
-- Name: contractor_bank_account fknu6wqcsueurewprgmq0bxhjr6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor_bank_account
    ADD CONSTRAINT fknu6wqcsueurewprgmq0bxhjr6 FOREIGN KEY (contractor_id) REFERENCES public.contractor(contractor_id);


--
-- Name: arranged_job fko799453pku9s8wx0ph44gsbiy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job
    ADD CONSTRAINT fko799453pku9s8wx0ph44gsbiy FOREIGN KEY (contract_id) REFERENCES public.contract(contract_id);


--
-- Name: equipment fkog1e3ls88y65004cs34gtncgs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipment
    ADD CONSTRAINT fkog1e3ls88y65004cs34gtncgs FOREIGN KEY (equipment_type_id) REFERENCES public.equipment_type(equipment_type_id);


--
-- Name: human_resource fkosdswe3qpmb12p5arob8m8vjj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.human_resource
    ADD CONSTRAINT fkosdswe3qpmb12p5arob8m8vjj FOREIGN KEY (sector_id) REFERENCES public.sector(sector_id);


--
-- Name: contract fkpk77py2qj16mler5ijifnkmox; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract
    ADD CONSTRAINT fkpk77py2qj16mler5ijifnkmox FOREIGN KEY (investor_id) REFERENCES public.investor(investor_id);


--
-- Name: preliminary_plan_mechanization fkq2ikjei4mavhup2x3jc6focyo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_mechanization
    ADD CONSTRAINT fkq2ikjei4mavhup2x3jc6focyo FOREIGN KEY (mechanization_type_id) REFERENCES public.mechanization_type(mechanization_type_id);


--
-- Name: human_resource fkqigdgye5ndlqhxn81pefb495j; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.human_resource
    ADD CONSTRAINT fkqigdgye5ndlqhxn81pefb495j FOREIGN KEY (job_type_id) REFERENCES public.job_type(job_type_id);


--
-- Name: location fkr2gdhhu8rhyrvslukhtfbg8v5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.location
    ADD CONSTRAINT fkr2gdhhu8rhyrvslukhtfbg8v5 FOREIGN KEY (city_id) REFERENCES public.city(city_id);


--
-- Name: preliminary_plan_equipment fkradfgrw677jnf1ol4lu5i6ffs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.preliminary_plan_equipment
    ADD CONSTRAINT fkradfgrw677jnf1ol4lu5i6ffs FOREIGN KEY (preliminary_plan_id) REFERENCES public.preliminary_plan(preliminary_plan_id);


--
-- Name: annex fkrgbx607w7vc9tdesuxlmt3w82; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annex
    ADD CONSTRAINT fkrgbx607w7vc9tdesuxlmt3w82 FOREIGN KEY (contract_id) REFERENCES public.contract(contract_id);


--
-- Name: offer fkslueps8n40qg8t0vk96v4f6my; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fkslueps8n40qg8t0vk96v4f6my FOREIGN KEY (contract_id) REFERENCES public.contract(contract_id);


--
-- Name: user_jadran fksqkxpfgv6sn1ndm2ybmqg87vm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_jadran
    ADD CONSTRAINT fksqkxpfgv6sn1ndm2ybmqg87vm FOREIGN KEY (sector_id) REFERENCES public.sector(sector_id);


--
-- Name: arranged_job fkt2td74tssh702els2khps70pi; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job
    ADD CONSTRAINT fkt2td74tssh702els2khps70pi FOREIGN KEY (chief_user_id) REFERENCES public.user_jadran(user_id);


--
-- Name: offer fkt35cpugplim2gsevgd8kug2gs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offer
    ADD CONSTRAINT fkt35cpugplim2gsevgd8kug2gs FOREIGN KEY (city_id) REFERENCES public.city(city_id);


--
-- Name: arranged_job fkt3tp89ofw1bl4ws089kiwfrsa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job
    ADD CONSTRAINT fkt3tp89ofw1bl4ws089kiwfrsa FOREIGN KEY (preliminary_plan_id) REFERENCES public.preliminary_plan(preliminary_plan_id);


--
-- Name: mechanization_project_day fktd33gqoobaf6bchqf0tshs4n3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mechanization_project_day
    ADD CONSTRAINT fktd33gqoobaf6bchqf0tshs4n3 FOREIGN KEY (mechanization_id) REFERENCES public.mechanization(mechanization_id);


--
-- Name: investor_real_estates fktj59jneoirhvgsnn9no2gqb61; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.investor_real_estates
    ADD CONSTRAINT fktj59jneoirhvgsnn9no2gqb61 FOREIGN KEY (real_estates_real_estate_id) REFERENCES public.real_estate(real_estate_id);


--
-- Name: worker_project_day fktky5aeo9ae7imjpxj81k56iok; Type: FK CONSTRAINT; Schema: public; Owner: nikola
--

ALTER TABLE ONLY public.worker_project_day
    ADD CONSTRAINT fktky5aeo9ae7imjpxj81k56iok FOREIGN KEY (human_resource_id) REFERENCES public.human_resource(human_resource_id);


--
-- Name: contractor fkto23io1r1xpryguq4ubwbp27u; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contractor
    ADD CONSTRAINT fkto23io1r1xpryguq4ubwbp27u FOREIGN KEY (location_id) REFERENCES public.location(location_id);


--
-- Name: arranged_job fktpc5nb49vmn9c0xt0sx7sdnar; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arranged_job
    ADD CONSTRAINT fktpc5nb49vmn9c0xt0sx7sdnar FOREIGN KEY (sector_id) REFERENCES public.sector(sector_id);


--
-- PostgreSQL database dump complete
--

