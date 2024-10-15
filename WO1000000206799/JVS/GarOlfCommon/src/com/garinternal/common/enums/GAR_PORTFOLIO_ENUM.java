package com.garinternal.common.enums;

/*
File Name:                      GAR_PORTFOLIO_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Portfolio

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.fnd.RefBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "portfolio", stringColumn = "name", dBColumns = { "id_number", "portfolio_type" },
    methods = { "toInt", "getPortfolioTypeInt" })

public enum GAR_PORTFOLIO_ENUM {
    //@formatter:off
    //                             Name                             Id           Portfolio type
	DS_TREASURY                   ("DS TREASURY",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	OLEO_CHEM                     ("OLEO CHEM",                     -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	CORPORATE                     ("CORPORATE",                     -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	R1                            ("R1",                            -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	PLANTATION                    ("PLANTATION",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	TRADING_INDIA_P               ("TRADING INDIA-P",               -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	TECH_ALGO_P                   ("TECH ALGO-P",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	FREIGHT_P                     ("FREIGHT-P",                     -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	TRADING_CHINA_P               ("TRADING CHINA-P",               -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	ORIGINATION_P                 ("ORIGINATION-P",                 -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	REFINERY_P                    ("REFINERY-P",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	A2_P                          ("A2 - P",                        -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P11                           ("P11",                           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P11A                          ("P11A",                          -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P9                            ("P9",                            -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P9D                           ("P9D",                           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	A1                            ("A1",                            -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	A3                            ("A3",                            -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	MARKETING_L                   ("MARKETING-L",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	TRADING_L                     ("TRADING-L",                     -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	FREIGHT_L                     ("FREIGHT-L",                     -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	REFINERY_L                    ("REFINERY-L",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	CRUSH                         ("CRUSH",                         -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	CHINA_BEAN_COMPLEX            ("CHINA BEAN COMPLEX",            -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	BASIS_TRADING                 ("BASIS TRADING",                 -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	CHINA_LOCAL_TRADING           ("CHINA LOCAL TRADING",           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	BIODIESEL                     ("BIODIESEL",                     -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	INDUSTRIAL                    ("INDUSTRIAL",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCP                           ("SCP",                           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	MAIN_BOOK                     ("MAIN BOOK",                     -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	MAR_MAY                       ("MAR/MAY",                       -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	JUL_SEP                       ("JUL/SEP",                       -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	NOV_JAN                       ("NOV/JAN",                       -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	DS1                           ("DS1",                           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	DS2                           ("DS2",                           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	DS3                           ("DS3",                           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	DS4                           ("DS4",                           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P9S_GAI                       ("P9S-GAI",                       -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P9S_IB                        ("P9S-IB",                        -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P11S                          ("P11S",                          -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	R2                            ("R2",                            -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	TRADING_CHINA_CO              ("TRADING CHINA-CO",              -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P9WS_GAI                      ("P9WS-GAI",                      -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	TRADING_OTH                   ("TRADING-OTH",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	OLEO_IB                       ("OLEO-IB",                       -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	TRADING_L2                    ("TRADING-L2",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	P10                           ("P10",                           -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	TRADING_WITHOUT_INT_SCHEDULE  ("TRADING WITHOUT INT SCHEDULE",  -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_2                    ("Scenario 2",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_3                    ("Scenario 3",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_4                    ("Scenario 4",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_5                    ("Scenario 5",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_6                    ("Scenario 6",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_7                    ("Scenario 7",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_8                    ("Scenario 8",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_9                    ("Scenario 9",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_10                   ("Scenario 10",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_1                    ("Scenario 1",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO11                    ("Scenario11",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_12                   ("Scenario 12",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_13                   ("Scenario 13",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_14                   ("Scenario 14",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_15                   ("Scenario 15",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	SCENARIO_16                   ("Scenario 16",                   -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	BACKSTOP_1                    ("Backstop 1",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	BACKSTOP_2                    ("Backstop 2",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	BACKSTOP_3                    ("Backstop 3",                    -1,          GAR_PORTFOLIO_TYPE_ENUM.TRADING),
	;
    //@formatter:on

    private final String            name;
    private int                     id;
    private GAR_PORTFOLIO_TYPE_ENUM portfolioType;

    GAR_PORTFOLIO_ENUM(String name, int id, GAR_PORTFOLIO_TYPE_ENUM portfolioType) {
        this.name          = name;
        this.id            = id;
        this.portfolioType = portfolioType;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns ID of the Enum
     *
     * @return Return ID of the portfolio
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE, this.name);
        }

        return idLocal;
    }

    /**
     * Returns {@link GAR_PORTFOLIO_TYPE_ENUM} enum
     *
     * @return
     */
    public GAR_PORTFOLIO_TYPE_ENUM getPortfolioType() {
        return this.portfolioType;
    }

    /**
     * Returns Portfolio Type Id
     *
     * @return
     * @throws OException
     */
    public int getPortfolioTypeInt() throws OException {
        return this.portfolioType.toInt();
    }
}
