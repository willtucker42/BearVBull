import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from numpy import long

cred = credentials.Certificate('./bearvbull_service_account_key.json')

# app = firebase_admin.initialize_app(cred)

db = firestore.client()

win_mult_dict = {}  # {String: Pair} | Ex. { "market_id": (40,60) }


def updateMarketsAndGetMarketIds():
    market_ids = []
    markets = db.collection(u'live_prediction_market_info').where(u'bet_status', u'==', "live").stream()
    for market in markets:
        market_dict = market.to_dict()
        mkt_id = market.id
        print("live market: ", mkt_id, "bear_total: ", market_dict['bear_total'], "-- bull_total: ",
              market_dict['bull_total'])
        addToMultDict(calculateWinMultipliers(market_dict['bear_total'], market_dict['bull_total']), mkt_id)
        market_ids.append(mkt_id)
        market.reference.update({u'bet_status': 'waiting'})
    return market_ids


def addToMultDict(pair, mkt_id):
    print("Adding pair: ", pair, " to and: ", mkt_id, " to dict")
    win_mult_dict[mkt_id] = pair


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
    print("updateUserBets market id: ", mkt_id)
    bear_bull_mult_pair = win_mult_dict[mkt_id]
    bets = db.collection(u'all_user_bets').where(u'market_id', u'==', mkt_id).stream()
    batch = db.batch()
    i = 0
    for bet in bets:
        bet_dict = bet.to_dict()
        bear_or_bull = bet_dict["bet_side"]
        print("Bet amount:", bet_dict["bet_amount"], ". Bet ticker: ", bet_dict["ticker_symbol"])
        if bear_or_bull == "BEAR":
            print("updating bet bear")
            batch.update(bet.reference, {u'win_multiplier': bear_bull_mult_pair[0]})
        else:
            print("updating bet bull")
            batch.update(bet.reference, {u'win_multiplier': bear_bull_mult_pair[1]})
        i += 1
        if i == 499:
            print("Committing and resetting user_bet batch...")
            batch.commit()
            batch = db.batch()
            i = 0
    batch.commit()


for market_id in updateMarketsAndGetMarketIds():
    updateUserBets(market_id)

# calculateWinMultipliers(18246799, 1)
