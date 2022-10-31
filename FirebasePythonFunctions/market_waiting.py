import math

import yfinance as yf
import firebase_admin
import datetime
from firebase_admin import credentials
from firebase_admin import firestore
from numpy import long

cred = credentials.Certificate('./bearvbull_service_account_key.json')

app = firebase_admin.initialize_app(cred)

db = firestore.client()

win_mult_dict = {}  # { "market_id": (40,60) } String: Pair


def updateMarketsAndGetMarketIds():
    market_ids = []
    markets = db.collection(u'live_prediction_market_info').where(u'bet_status', u'==', "live").stream()
    for market in markets:
        market_dict = market.to_dict()
        mkt_id = market_dict['market_id']
        print("live market: ", mkt_id)
        addToMultDict(calculateWinMultipliers(market_dict['bear_total'], market_dict['bull_total']), mkt_id)
        market_ids.append(mkt_id)
        market.reference.update({u'bet_status': 'waiting'})
    return market_ids


def addToMultDict(pair, market_id):
    print("Adding pair: ", pair, " to and: ", market_id, " to dict")
    win_mult_dict[market_id] = pair


def calculateWinMultipliers(bear_total, bull_total):
    total_wagered = long(bear_total) + long(bull_total)
    if total_wagered == 0:
        return 1, 1
    try:
        print("bear percent")
        bear_percent = (float(bear_total) / float(total_wagered)) * 100
        bull_percent = (float(bull_total) / float(total_wagered)) * 100

        print("bear percent", bear_percent)
        print("bull percent", bull_percent)

        bear_mult = round(100 / bear_percent, 4)
        bull_mult = round(100 / bull_percent, 4)
        print("bear mult", bear_mult)
        print("bull mult", bull_mult)

        return bear_mult, bull_mult
    except:
        print("returning 1,1")
        return 1, 1


def updateUserBets(mkt_id):
    bear_bull_mult_pair = win_mult_dict[mkt_id]


# for market_id in updateMarketsAndGetMarketIds():
#     updateUserBets(market_id)

calculateWinMultipliers(18246799, 1)
