import firebase_admin
import yfinance as yf
import datetime
from flask import Flask
from firebase_admin import credentials
from firebase_admin import firestore
import create_new_market as cnm

# Use a service account.
cred = credentials.Certificate('./bearvbull_service_account_key.json')

# app = firebase_admin.initialize_app(cred)
app = cnm.app

db = firestore.client()


# new_market_tickers = ["SPY", "TSLA", "MSFT", "AAPL", "GOOG"]
# new_market_tickers = ["asfd", "54h54", "ffdg", "sdafg", "2435t", "gabababa"]


def getTodaysOpen(ticker):
    print("Getting todays open for ticker: " + ticker)
    ticker_info = yf.Ticker(ticker).info
    try:
        todaysMarketOpen = ticker_info['regularMarketOpen']
        previousMarketClose = ticker_info['regularMarketPreviousClose']
    except:
        print("Error getting ticker info")
        return
    print('todays market open: ' + str(todaysMarketOpen))
    print('previous market open: ' + str(previousMarketClose))
    if todaysMarketOpen >= previousMarketClose:
        update('green', ticker)
    elif todaysMarketOpen < previousMarketClose:
        update('red', ticker)
    else:
        print('error1')


def update(open_color, ticker):
    print("Starting ", open_color, " open update process for ticker", ticker)
    user_winnings_dict = {}
    docs = db.collection(u'all_user_bets').where(u'bet_status', u'==', "active").where(u'ticker_symbol', u'==',
                                                                                       ticker).stream()
    winnings = 0
    i = 0
    all_user_bet_batch = db.batch()
    for doc in docs:
        bet_dict = doc.to_dict()
        if bet_dict['bet_side'] == 'BULL' and open_color == 'green':
            print("user is a bull")
            winnings = bet_dict['win_multiplier'] * bet_dict['bet_amount']
            # print("User ", bet_dict['user_id'], " won ", winnings, "(", bet_dict['win_multiplier'], " * ",
            #       bet_dict['bet_amount'], ")")
            all_user_bet_batch.update(doc.reference, {
                u'bet_status': "won"
            })
        elif bet_dict['bet_side'] == 'BEAR' and open_color == 'red':
            print("user is a bear")
            winnings = bet_dict['win_multiplier'] * bet_dict['bet_amount']
            # print("User ", bet_dict['user_id'], " won ", winnings, "(", bet_dict['win_multiplier'], " * ",
            #       bet_dict['bet_amount'], ")")
            all_user_bet_batch.update(doc.reference, {
                u'bet_status': "won"
            })
        else:
            all_user_bet_batch.update(doc.reference, {
                u'bet_status': "lost"
            })  # This means that they lost
        i += 1
        if i == 499:
            # right here we commit the batch and create a new instance of a batch, because max batch size is 500
            print("committing and resetting all_user_bet_batch...")
            all_user_bet_batch.commit()
            all_user_bet_batch = db.batch()
            i = 0

        user_winnings_dict[bet_dict['user_id']] = winnings
    all_user_bet_batch.commit()
    updateUserAccounts(user_winnings_dict=user_winnings_dict)


def updateUserAccounts(user_winnings_dict):
    # user_id in user_bet document == username in users document
    i = 0
    batch = db.batch()
    for thing in user_winnings_dict:
        print(thing)

    for user in user_winnings_dict.keys():
        print("Updating user: ", user, ". They won ", user_winnings_dict[user])
        user_doc = db.collection(u'users').where(u'user_id', u'==', user).limit(1).get()
        for doc in user_doc:
            user_dict = doc.to_dict()
            og_balance_avail = user_dict['balance_available']
            og_elo_score = user_dict['elo_score']
            marketPoints = -10
            if user_winnings_dict[user] != 0:
                # if the winnings is not zero that means they won and get marketPoints
                marketPoints = 15
            # user_doc.reference.update()
            batch.update(doc.reference, {
                u'balance_available': round(og_balance_avail + user_winnings_dict[user]),
                u'elo_score': (og_elo_score + marketPoints)
            })
            print("Their balance is:", round(og_balance_avail + user_winnings_dict[user]))
            print("Their OG elo score was ", og_elo_score, ". Market points to add is", marketPoints)
            print("Their current elo score is:", (og_elo_score + marketPoints))

        i += 1
        if i == 499:
            # right here we commit the batch and create a new instance of a batch, because max batch size is 500
            print("committing and resetting batch...")
            batch.commit()
            batch = db.batch()
            i = 0
    batch.commit()
    # user_doc.update()


def finishMarkets():
    current_live_markets = db.collection(u'live_prediction_market_info').where(u'bet_status', u'==', 'live')


def getWaitingMarkets():
    # Returns a list of the active market tickers
    tickers = []
    active_markets = db.collection(u'live_prediction_market_info').where(u'bet_status', u'==', "waiting").stream()
    for market in active_markets:
        market_dict = market.to_dict()
        ticker = market_dict['ticker']
        print("Waiting ticker: ", ticker)
        tickers.append(ticker)
        market.reference.update({u'bet_status': 'finished'})
    return tickers


def isTodayAValidStockMarketDay():
    # Get today's date in datetime format
    stock_market_day_nums = [0, 1, 2, 3, 4]
    today = datetime.datetime.today()
    day_of_week_nums = today.weekday()
    if day_of_week_nums not in stock_market_day_nums:
        return False

    # if todayIsAHoliday(): return false
    return True


# First update the existing markets, bets
for tikr in getWaitingMarkets():
    getTodaysOpen(tikr)
cnm.createNewMarkets()

# Then add the new markets for the day
# for new_market in new_market_tickers:
#     cnm.createNewMarket(new_market)
# if isTodayAValidStockMarketDay():
# getTodaysOpen('SPY')
