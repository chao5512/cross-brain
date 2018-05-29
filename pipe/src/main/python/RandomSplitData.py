
def split_data(data, weights, seed=None):
    splits = data.randomSplit(weights, seed)
    return splits