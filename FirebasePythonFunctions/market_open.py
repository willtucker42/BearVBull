import yfinance as yf
import firebase_admin
import datetime
from firebase_admin import credentials
from firebase_admin import firestore
import create_new_market as cnm

# Use a service account.
cred = credentials.Certificate('./bearvbull_service_account_key.json')

app = cnm.app

db = firestore.client()

# new_market_tickers = ["SPY", "TSLA", "MSFT", "AAPL", "GOOG"]
# new_market_tickers = ["asfd", "54h54", "ffdg", "sdafg", "2435t", "gabababa"]


def getTodaysOpen(ticker):
    ticker_info = yf.Ticker(ticker).info
    try:
        todaysMarketOpen = ticker_info['regularMarketOpen']
        previousMarketClose = ticker_info['regularMarketPreviousClose']
    except:
        print("Error getting ticker info")
        return

    if todaysMarketOpen >= previousMarketClose:
        update('green', ticker)
    elif todaysMarketOpen < previousMarketClose:
        update('red', ticker)
    else:
        print('error1')

    print('todays market open: ' + str(todaysMarketOpen))
    print('previous market open: ' + str(previousMarketClose))


def update(open_color, ticker):
    print("Starting green open update process")
    user_winnings_dict = {}
    docs = db.collection(u'all_user_bets').where(u'bet_status', u'==', "active").where(u'ticker_symbol', u'==',
                                                                                       ticker).stream()
    winnings = 0
    for doc in docs:
        bet_dict = doc.to_dict()
        if bet_dict['bet_side'] == 'BULL' and open_color == 'green':
            winnings = bet_dict['win_multiplier'] * bet_dict['bet_amount']
            # print("User ", bet_dict['user_id'], " won ", winnings, "(", bet_dict['win_multiplier'], " * ",
            #       bet_dict['bet_amount'], ")")
        elif bet_dict['bet_side'] == 'BEAR' and open_color == 'red':
            winnings = bet_dict['win_multiplier'] * bet_dict['bet_amount']
            # print("User ", bet_dict['user_id'], " won ", winnings, "(", bet_dict['win_multiplier'], " * ",
            #       bet_dict['bet_amount'], ")")
        else:
            pass
            # print("User", bet_dict['user_id'], " lost ", bet_dict['bet_amount'])
        user_winnings_dict[bet_dict['user_id']] = winnings

    updateUserAccounts(user_winnings_dict=user_winnings_dict)


def updateUserAccounts(user_winnings_dict):
    # user_id in user_bet document == username in users document
    i = 0
    batch = db.batch()

    for thing in user_winnings_dict:
        print(thing)

    for user in user_winnings_dict.keys():
        print("Updating user: ", user, ". They won ", user_winnings_dict[user])
        user_doc = db.collection(u'users').where(u'username', u'==', user).limit(1).get()
        for doc in user_doc:
            user_dict = doc.to_dict()
            og_balance_avail = user_dict['balance_available']
            # user_doc.reference.update()
            batch.update(doc.reference, {u'balance_available': round(og_balance_avail + user_winnings_dict[user])})
            print("Their balance is:", round(og_balance_avail + user_winnings_dict[user]))

        i += 1
        if i == 499:
            # right here we commit the batch and create a new instance of a batch
            print("committing and resetting batch...")
            batch.commit()
            batch = db.batch()
            i = 0
    batch.commit()
    # user_doc.update()


def finishMarkets():
    current_live_markets = db.collection(u'live_prediction_market_info').where(u'bet_status', u'==', 'live')


def getActiveMarkets():
    # Returns a list of the active market tickers
    tickers = []
    active_markets = db.collection(u'live_prediction_market_info').where(u'bet_status', u'==', "live").stream()
    for market in active_markets:
        market_dict = market.to_dict()
        ticker = market_dict['ticker']
        print(ticker)
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

    # if nextDayIsAHoliday(): return false
    return True


# First update the existing markets, bets
for tikr in getActiveMarkets():
    getTodaysOpen(tikr)

# Then add the new markets for the day
# for new_market in new_market_tickers:
#     cnm.createNewMarket(new_market)
# if isTodayAValidStockMarketDay():
# getTodaysOpen('SPY')
