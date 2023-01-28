import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import matplotlib

dataBase = pd.read_csv('Google-Playstore.csv')
#dataBase.describe()

#__________________________________________________________________________________________________
# #Top 10 aplicações 
# def findtop10incategory(str):
#     str = str.upper()
#     top10 = dataBase[dataBase['Category'] == str]
#     top10apps = top10.sort_values(by='Maximum_Installs', ascending=False).head(10)
#     # Top_Apps_in_art_and_design
#     plt.figure(figsize=(15,12))
#     plt.title('Top 10 Installed Apps',size = 20);    
#     graph = sns.barplot(x = top10apps.App_Name, y = top10apps.Maximum_Installs)
#     graph.set_xticklabels(graph.get_xticklabels(), rotation= 45, horizontalalignment='right');
    
# findtop10incategory('Social')
#___________________________________________________________________________________________________

#_______________________________________________________________________________________________
#Top 10 categorias, sendo no eixo do X o Nº de aplicações de cada categoria
# y = dataBase['Category'].value_counts().index
# x = dataBase['Category'].value_counts()
# xsis = []
# ysis = []
# for i in range(len(x)):
#     xsis.append(x[i])
#     ysis.append(y[i])
    
# plt.figure(figsize=(18,13))
# plt.xlabel("Count")
# plt.ylabel("Category")

# graph = sns.barplot(x = xsis, y = ysis, palette= "husl")
# graph.set_title("Top categories on Google Playstore", fontsize =25 );

#____________________________________________________________________________________________________
#distribuição do Rating 
# plt.figure(figsize=(15,9))
# plt.xlabel("Rating")
# plt.ylabel("Frequency")
# graph = sns.kdeplot(dataBase.Rating, color="Blue", shade = True)
# plt.title('Distribution of Rating',size = 20);


# import urllib

# image_url = urllib.parse.urljoin('https://unsplash.com/', 'rating.jpg')
# print(image_url)
#_________________________________________________________________________________________________________

#not working
# labels = dataBase['Price'].value_counts(sort = True).index
# sizes = dataBase['Price'].value_counts(sort = True)
# colors = ["blue","lightgreen"]
# explode = (0.2,0)
# plt.pie(sizes, explode=explode, labels=labels, colors=colors, autopct='%1.1f%%', shadow=True, startangle=0)
# plt.title('Percent of Free Vs Paid Apps in store',size = 20)
# plt.show()
#__________________________________________________________________________________________________________
#distribuição do Content Rating por um gráfico redondo
# import csv
# import matplotlib.pyplot as plt


# #Open the CSV file
# with open('Google-Playstore.csv', 'r', encoding="utf8") as file:
#     # Create a CSV reader
#     reader = csv.reader(file)
    
#     # Initialize the counts
#     teen_count = 0
#     everyone_count = 0
#     adult_count =0
#     ten_count=0
#     mature_count=0
#     Unrated_count =0
    
#     # Iterate through the rows
#     for row in reader:
#         # Get the value in the column
#         value = row[18]  # Assumes that the column you are interested in is the third column (index 2)
        
#         # Increment the count for the appropriate value
#         if value == 'Teen':
#             teen_count += 1
#         elif value == 'Everyone':
#             everyone_count += 1
#         elif value == 'Adults only 18+':
#             adult_count += 1
#         elif value == 'Everyone 10+':
#             ten_count += 1
#         elif value == 'Mature 17+':
#             mature_count += 1
#         elif value == 'Unrated':
#             Unrated_count += 1

# # Print the counts
# print(f'Number of teens: {teen_count}')
# print(f'Number of everyone: {everyone_count}')
# print(f'Number of adult: {adult_count}')
# print(f'Number of Everyone 10+: {ten_count}')
# print(f'Number of mature 17+: {mature_count}')
# print(f'Number of Unrated: {Unrated_count}')

# counts = [mature_count, teen_count, everyone_count, adult_count,Unrated_count, ten_count]
# labels = ['Teen', 'Everyone', 'Adult only 18+', 'Everyone 10+', 'Mature 17+', 'Unrated_count']
# plt.title('Distribution of Content Type')
# plt.pie(counts, labels=labels, autopct='%1.1f%%')

# plt.show()
#__________________________________________________________________________________________________________
#distribuição do Content Rating por um gráfico redondo
# import csv
# import matplotlib.pyplot as plt


# # Open the CSV file
# with open('Google-Playstore.csv', 'r', encoding="utf8") as file:
#     # Create a CSV reader
#     reader = csv.reader(file)
    
#     # Initialize the counts
#     free_count = 0
#     paid_count =0
#     # Iterate through the rows
#     for row in reader:
#         # Get the value in the column
#         value = row[9]  # Assumes that the column you are interested in is the third column (index 2)
        
#         # Increment the count for the appropriate value
#         if value == '0':
#             free_count += 1
#         else: 
#             paid_count += 1


# # Print the counts
# print(f'Number of free: {free_count}')
# print(f'Number of paid: {paid_count}')


# counts = [free_count, paid_count]
# labels = ['Free', 'Paid']
# plt.title('Free Vrs Paid Apps')
# plt.pie(counts, labels=labels, autopct='%1.1f%%')

# plt.show()
#____________________________________________________________________________________________________________
#Free vrs Paid
# import csv
# import matplotlib.pyplot as plt


# #Open the CSV file
# with open('Google-Playstore.csv', 'r', encoding="utf8") as file:
#     # Create a CSV reader
#     reader = csv.reader(file)
    
#     # Initialize the counts
#     teen_count = 0
#     everyone_count = 0
#     adult_count =0
#     ten_count=0
#     mature_count=0
#     Unrated_count =0
    
#     # Iterate through the rows
#     for row in reader:
#         # Get the value in the column
#         value = row[18]  # Assumes that the column you are interested in is the third column (index 2)
        
#         # Increment the count for the appropriate value
#         if value == 'Teen':
#             teen_count += 1
#         elif value == 'Everyone':
#             everyone_count += 1
#         elif value == 'Adults only 18+':
#             adult_count += 1
#         elif value == 'Everyone 10+':
#             ten_count += 1
#         elif value == 'Mature 17+':
#             mature_count += 1
#         elif value == 'Unrated':
#             Unrated_count += 1

# # Print the counts
# print(f'Number of teens: {teen_count}')
# print(f'Number of everyone: {everyone_count}')
# print(f'Number of adult: {adult_count}')
# print(f'Number of Everyone 10+: {ten_count}')
# print(f'Number of mature 17+: {mature_count}')
# print(f'Number of Unrated: {Unrated_count}')

# counts = [mature_count, teen_count, everyone_count, adult_count,Unrated_count, ten_count]
# labels = ['Teen', 'Everyone', 'Adult only 18+', 'Everyone 10+', 'Mature 17+', 'Unrated_count']
# plt.title('Distribution of Content Type')
# plt.pie(counts, labels=labels, autopct='%1.1f%%')

# plt.show()
#_____________________________________________________________________________________________________________
#Top 10 categorias, sendo no eixo do X o Nº de aplicações de cada categoria
y = dataBase['Developer Id'].value_counts().index
x = dataBase['Maximum Installs'].value_counts()
xsis = []
ysis = []
for i in range(10):
    xsis.append(x[i])
    ysis.append(y[i])
    
plt.figure(figsize=(13,25))
plt.xlabel("Sum of Downloads")
plt.ylabel("Developers ")

graph = sns.barplot(x = xsis, y = ysis, palette= "husl")
graph.set_title("Top Developers on Google Playstore", fontsize =25 );
#_____________________________________________________________________________________________________________
#Top 10 categorias, sendo no eixo do X o Nº de aplicações de cada categoria
# y = dataBase['Developer Id'].value_counts().index
# x = dataBase['App Name'].value_counts()
# xsis = []
# ysis = []
# for i in range(10):
#     xsis.append(x[i])
#     ysis.append(y[i])
    
# plt.figure(figsize=(13,25))
# plt.xlabel("Number of Apps")
# plt.ylabel("Developers ")

# graph = sns.barplot(x = xsis, y = ysis, palette= "husl")
# graph.set_title("Top Developers on Google Playstore", fontsize =25 );