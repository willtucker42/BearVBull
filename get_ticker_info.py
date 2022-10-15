import yfinance as yf

def getTodaysOpen(ticker):
	ticker_info = yf.Ticker(ticker).info
	#print(ticker_info.keys())
		
	todaysMarketOpen = ticker_info['regularMarketOpen']
	previousMarketClose = ticker_info['regularMarketPreviousClose']
	
	if (todaysMarketOpen >= previousMarketClose):
		print('The market opened green today.')
	elif (todaysMarketOpen < previousMarketClose):
		print('The market opened red today.')
	else:
		print('error1')
	print('todays market open: ' + str(todaysMarketOpen))
	print('previous market open: ' + str(previousMarketClose))

getTodaysOpen('SPY')