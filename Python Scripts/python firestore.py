

import pandas as pd
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from getImagefromGoogleScript import *

#cred = credentials.Certificate('./serviceAccountKey.json')

#firebase_admin.initialize_app(cred)
#db = firestore.client()
#doc_ref = db.collection('applications')



# Import data
dataset = pd.read_csv("dataset.csv")


tmp = dataset.to_dict(orient='records')
list(map(lambda x: doc_ref.add(x), tmp))


